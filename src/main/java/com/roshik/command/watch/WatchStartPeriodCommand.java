package com.roshik.command.watch;

import com.roshik.command.ICommand;
import com.roshik.command.ICommandValidator;
import com.roshik.command.IHasNextCommand;
import com.roshik.command.ValidationResult;
import com.roshik.command.create.Storage;
import com.roshik.domains.TaskStatus;
import com.roshik.services.FilterTaskQuery;
import com.roshik.services.KeyBoardService;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;

@ComponentScan
@Service
@Scope(value = "prototype")
public class WatchStartPeriodCommand implements ICommand, ICommandValidator, IHasNextCommand {
    private final KeyBoardService keyBoardService;
    private final Storage storage;
    private Long currentChatId;


    public WatchStartPeriodCommand(KeyBoardService keyBoardService, Storage storage) {
        this.keyBoardService = keyBoardService;
        this.storage = storage;
    }

    @Override
    public SendMessage generateRequest(Long chatId) {
        currentChatId = chatId;
        SendMessage sendMessage = new SendMessage()
                .setChatId(currentChatId)
                .setText("Введи с какой даты хочешь посмотреть задачи в формате День.Месяц.Год");
        return sendMessage;
    }

    @Override
    public void handleResponse(String message) {
        var selectTaskQuery = (FilterTaskQuery) storage.getTempObject(currentChatId);
        selectTaskQuery.setStart_date(message);
        storage.saveTempObject(currentChatId, selectTaskQuery);
    }

    @Override
    public ValidationResult validateMessage(String message, Long chatId) {

        var result = new ValidationResult();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        dateFormat.setLenient(false);
        try {
            dateFormat.parse(message.trim());
            result.IsSuccess = true;
        } catch (ParseException pe) {
            result.IsSuccess = false;
            result.ValidationError = "Неверный формат даты, введи в формате День.Месяц.Год, например 12.12.2012";
        }
        return result;
    }

    @Override
    public Class<?> getNextCommandName() {
        return WatchEndPeriodCommand.class;
    }
}
