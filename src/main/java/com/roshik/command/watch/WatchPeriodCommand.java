package com.roshik.command.watch;

import com.roshik.command.*;
import com.roshik.command.create.Storage;
import com.roshik.services.*;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;

import java.util.*;

@ComponentScan
@Service
@Scope(value = "prototype")
public class WatchPeriodCommand implements ICommand, ICommandValidator, IHasNextCommand {

    private final Map<String, Class<?>> menu = Map.of(
            "За все время", MainMenuCommand.class,
            "Установить период", WatchStartPeriodCommand.class
    );
    private Class<?> NextCommandHandlerName;
    private final KeyBoardService keyBoardService;
    private final Storage storage;
    private final TasksByQuery tasksByQuery;
    private Long currentChatId;

    public WatchPeriodCommand(KeyBoardService keyBoardService, Storage storage, TasksByQuery tasksByQuery) {
        this.keyBoardService = keyBoardService;
        this.storage = storage;
        this.tasksByQuery = tasksByQuery;
    }


    @Override
    public SendMessage generateRequest(Long chatId) {
        currentChatId = chatId;
        ReplyKeyboardMarkup keyboard = keyBoardService.getKeyboard(new TreeSet<>(menu.keySet()));
        var message = keyBoardService.createMessage(chatId, "Выбери период");
        message.setReplyMarkup(keyboard);
        return message;
    }

    @Override
    public void handleResponse(String message) {
        if(message.equals("Установить период")) {
            NextCommandHandlerName = WatchStartPeriodCommand.class;
        }
        else {
            var selectTaskQuery = (FilterTaskQuery) storage.getTempObject(currentChatId);
            storage.saveTempObject(currentChatId, selectTaskQuery);
            tasksByQuery.sendTasksByQuery(selectTaskQuery,currentChatId);
            NextCommandHandlerName=tasksByQuery.getNextCommandHandlerName();

        }
    }

    @Override
    public ValidationResult validateMessage(String message, Long chatId) {
        var result = new ValidationResult();
        if (!menu.containsKey(message)) {
            result.IsSuccess = false;
            result.ValidationError = "Такой команды нет";
        } else {
            result.IsSuccess = true;
        }
        return result;
    }

    @Override
    public Class<?> getNextCommandName() {
        return NextCommandHandlerName;
    }
}
