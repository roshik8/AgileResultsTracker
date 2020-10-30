package com.roshik.command.watch;

import com.roshik.command.*;
import com.roshik.services.KeyBoardService;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;

import java.util.Map;

@ComponentScan
@Service
@Scope(value = "prototype")
public class WatchTaskCommand implements ICommand, ICommandValidator, IHasNextCommand {
    private final KeyBoardService keyBoardService;
    //private final AgileResultsBot agileResultsBot;
    private Long currentChatId;
    private final Map<String, Class<?>> menu = Map.of(
            "Свои задачи", GetSelfTask.class,
            "Чужие задачи", GetPermissionTask.class
    );
    private String NextCommandHandlerName;

    public WatchTaskCommand(KeyBoardService keyBoardService) {
        this.keyBoardService = keyBoardService;
        //this.agileResultsBot = agileResultsBot;
    }

    @Override
    public SendMessage generateRequest(Long chatId) {
        currentChatId = chatId;
        ReplyKeyboardMarkup keyboard = keyBoardService.getKeyboard(menu.keySet());
        var message = keyBoardService.createMessage(chatId, "Какие задачи хочешь посмотреть?");
        message.setReplyMarkup(keyboard);
        return message;
    }

    @Override
    public void handleResponse(String message) {
        NextCommandHandlerName = message;
    }

    @Override
    public ValidationResult ValidateMessage(String message) {
        return null;
    }

    @Override
    public Class<?> getNextCommandName() {
        return menu.get(NextCommandHandlerName);
    }
}
