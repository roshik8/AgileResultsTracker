package com.roshik.command;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.api.methods.send.SendMessage;

@ComponentScan
@Service
@Scope(value = "prototype")
public class CreateTaskCommand implements ICommand{


    public CreateTaskCommand() {
    }

    @Override
    public SendMessage InitOutgoingMessage(Long chatId) {
        SendMessage sendMessage = new SendMessage() // Create a SendMessage object with mandatory fields
                .setChatId(chatId)
        .setText("Как дела?");
        return sendMessage;
    }

    @Override
    public void HandleIncomingMessage(String message) {

    }
}
