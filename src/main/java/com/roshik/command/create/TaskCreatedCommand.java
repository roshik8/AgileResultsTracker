package com.roshik.command.create;

import com.roshik.bot.AgileResultsBot;
import com.roshik.command.ICommand;
import com.roshik.command.IHasNextCommand;
import com.roshik.command.MainMenuCommand;
import com.roshik.command.ValidationResult;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;

@ComponentScan
@Service
@Scope(value = "prototype")
public class TaskCreatedCommand implements ICommand, IHasNextCommand {

    private final AgileResultsBot agileResultsBot;
    private Long currentChatId;

    private String NextCommandHandlerName;

    public TaskCreatedCommand(AgileResultsBot agileResultsBot) {
        this.agileResultsBot = agileResultsBot;
    }

    @Override
    public SendMessage generateRequest(Long chatId) {
        currentChatId = chatId;
        SendMessage sendMessage = new SendMessage()
                .setChatId(currentChatId)
                .setText("Задача создана");
        return sendMessage;
    }

    @Override
    public void handleResponse(String message) {
        NextCommandHandlerName = message;
    }

    //@Override
    public ValidationResult ValidateMessage(String message) {
        return null;
    }

    @Override
    public Class<?> getNextCommandName() {
        return MainMenuCommand.class;
    }
}
