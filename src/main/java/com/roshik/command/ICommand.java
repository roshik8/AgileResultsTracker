package com.roshik.command;

import org.telegram.telegrambots.api.methods.send.SendMessage;

public interface ICommand
{
    SendMessage InitOutgoingMessage(Long chatId);

    void HandleIncomingMessage(String message);
}