package com.roshik.bot;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;

public interface InputMessageHandler {
    SendMessage handle(Message message);

    BotState getHandlerName();
}
