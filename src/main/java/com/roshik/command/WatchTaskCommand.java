package com.roshik.command;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.api.methods.send.SendMessage;

@ComponentScan
@Service
@Scope(value = "prototype")
public class WatchTaskCommand implements ICommand {


    public WatchTaskCommand() {
    }

    @Override
    public SendMessage InitOutgoingMessage(Long chatId) {
        SendMessage sendMessage = new SendMessage()
                .setChatId(chatId)
                .setText("Вот они твои задачи пес");
        return sendMessage;
    }

    @Override
    public void HandleIncomingMessage(String message) {

    }
}
