package com.roshik.command;

import com.roshik.services.MainMenuService;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.api.methods.send.SendMessage;

import java.util.Map;

@ComponentScan
@Service
@Scope(value = "prototype")
public class MainMenuCommand implements ICommand, ICommandValidator, IHasNextCommand {

    private final MainMenuService mainMenuService;
    private final Map<String, String> menu = Map.of(
            "Создать задачу", "CreateTaskCommand",
            "Посмотреть задачи", "WatchTaskCommand"
    );
    private String NextCommandHandlerName;

    public MainMenuCommand(MainMenuService mainMenuService) {
        this.mainMenuService = mainMenuService;
    }

    @Override
    public SendMessage InitOutgoingMessage(Long chatId) {
        return mainMenuService.getMainMenuMessage(chatId, "Выбери пункт меню");
    }

    @Override
    public void HandleIncomingMessage(String message) {
        NextCommandHandlerName = message;
    }

    @Override
    public ValidationResult ValidateMessage(String message) {
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
    public String getNextCommandName() {
        return menu.get(NextCommandHandlerName);
    }
}