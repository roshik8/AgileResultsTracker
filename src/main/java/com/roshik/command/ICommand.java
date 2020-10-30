package com.roshik.command;

import org.telegram.telegrambots.api.methods.send.SendMessage;

public interface ICommand
{
    SendMessage generateRequest(Long chatId);

    void handleResponse(String message);
}