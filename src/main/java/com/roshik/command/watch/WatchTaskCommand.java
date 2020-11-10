package com.roshik.command.watch;

import com.roshik.command.ICommand;
import com.roshik.command.ICommandValidator;
import com.roshik.command.IHasNextCommand;
import com.roshik.command.ValidationResult;
import com.roshik.command.Storage;
import com.roshik.domains.TaskStatus;
import com.roshik.services.KeyBoardService;
import com.roshik.services.FilterTaskQuery;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;

import java.util.Map;
import java.util.TreeSet;

@ComponentScan
@Service
@Scope(value = "prototype")
public class WatchTaskCommand implements ICommand, ICommandValidator, IHasNextCommand {
    private final KeyBoardService keyBoardService;
    private final Storage storage;
    private Long currentChatId;
    private final Map<String, TaskStatus> menu = Map.of(
            "Открытые задачи", TaskStatus.Created,
            "Закрытые задачи", TaskStatus.Done,
            "Просроченные задачи",TaskStatus.Expired
    );

    public WatchTaskCommand(KeyBoardService keyBoardService, Storage storage) {
        this.keyBoardService = keyBoardService;
        this.storage = storage;
    }

    @Override
    public SendMessage generateRequest(Long chatId) {
        currentChatId = chatId;
        ReplyKeyboardMarkup keyboard = keyBoardService.getKeyboard(new TreeSet<>(menu.keySet()));
        var message = keyBoardService.createMessage(chatId, "Какие задачи хочешь посмотреть?");
        message.setReplyMarkup(keyboard);
        return message;
    }

    @Override
    public void handleResponse(String message) {
        var selectTaskQuery = (FilterTaskQuery) storage.getTempObject(currentChatId);
        selectTaskQuery.setStatus(menu.get(message));
        storage.saveTempObject(currentChatId, selectTaskQuery);
    }

    @Override
    public ValidationResult validateMessage(String message, Long chatId) {

        var result = new ValidationResult();
        if (StringUtils.isEmpty(message)||!menu.containsKey(message)) {
            result.IsSuccess = false;
            result.ValidationError = "Выбери команду из списка";
        }
        else {
            result.IsSuccess = true;
        }
        return result;
    }

    @Override
    public Class<?> getNextCommandName() {
        return WatchPeriodCommand.class;
    }
}
