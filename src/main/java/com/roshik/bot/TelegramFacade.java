package com.roshik.bot;

import com.google.gson.Gson;
import com.roshik.command.StateManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.api.methods.BotApiMethod;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Contact;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;

@Service
@Slf4j
public class TelegramFacade {
    private final StateManager stateManager;

    public TelegramFacade(StateManager stateManager) {
        this.stateManager = stateManager;
    }

    public BotApiMethod<?> handleUpdate(Update update) {
        Message message;
        String text;

        if (update.hasCallbackQuery()) {
            message = update.getCallbackQuery().getMessage();
            text = update.getCallbackQuery().getData();
        } else if (update.hasMessage()) {
            message = update.getMessage();
            text = message.getText();
        } else {
            return null;
        }

        Long chatId = message.getChatId();
        Integer messageId = message.getMessageId();
        if (text == null) {
            Contact contact = message.getContact();
            if (contact != null) {
                Gson gson = new Gson();
                text = gson.toJson(contact);
            }
        }

        try {
            var outMessage = stateManager.handleUpdate(chatId, messageId, text);
            return outMessage;
        } catch (Exception e) {
            SendMessage sendMessage = new SendMessage()
                    .setChatId(chatId)
                    .setText("Не понимаю, отправь команду");
            return sendMessage;
        }
    }
}
