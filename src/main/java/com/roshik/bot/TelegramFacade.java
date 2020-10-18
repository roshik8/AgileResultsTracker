package com.roshik.bot;

import com.roshik.command.StateManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;

@Service
@Slf4j
public class TelegramFacade {
    private final StateManager commandCore;

    public TelegramFacade(StateManager commandCore) {
        this.commandCore = commandCore;
    }

    public SendMessage handleUpdate(Update update) {
        Message message;
        if (update.hasCallbackQuery()) {
            message = update.getCallbackQuery().getMessage();
        } else {
            message = update.getMessage();
        }

        if (message == null) {
            // todo обработать непонятные сообщения
            return null;
        }

        var outMessage = commandCore.handleUpdate(message.getChatId(), message.getText());
        return outMessage;
    }

}
