package com.roshik.command;

import com.roshik.command.create.CreateTaskTitleCommand;
import com.roshik.command.watch.WatchPermissionCommand;
import com.roshik.services.KeyBoardService;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;

import java.util.Map;
import java.util.TreeSet;

@ComponentScan
@Service
@Scope(value = "prototype")
public class MainMenuCommand implements ICommand, ICommandValidator, IHasNextCommand {

    private final KeyBoardService keyBoardService;
    private final Map<String, Class<?>> menu = Map.of(
            "Создать задачу", CreateTaskTitleCommand.class,
            "Посмотреть задачи", WatchPermissionCommand.class
    );
    private String NextCommandHandlerName;

    public MainMenuCommand(KeyBoardService keyBoardService) {
        this.keyBoardService = keyBoardService;
    }

    @Override
    public SendMessage generateRequest(Long chatId) {
        ReplyKeyboardMarkup keyboard = keyBoardService.getKeyboard(new TreeSet<>(menu.keySet()));
        var message = keyBoardService.createMessage(chatId, "Выбери пункт меню");
        message.setReplyMarkup(keyboard);
        return message;
    }

    @Override
    public void handleResponse(String message) {
        NextCommandHandlerName = message;
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

        return menu.get(NextCommandHandlerName);
    }
}