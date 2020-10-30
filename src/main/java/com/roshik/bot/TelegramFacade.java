package com.roshik.bot;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.roshik.command.StateManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.api.methods.BotApiMethod;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageReplyMarkup;
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
            // todo обработать непонятные сообщения
            return null;
        }

        Long chatId = message.getChatId();
        if (text == null) {
            Contact contact = message.getContact();
            if (contact != null) {
                ObjectMapper mapper = new ObjectMapper();
                try {
                    text = mapper.writeValueAsString(contact);
                } catch (Exception ex) {
                    // todo обработать непонятные сообщения
                }
            }
        }
        try {

            var outMessage = stateManager.handleUpdate(chatId, text);

            if (update.hasCallbackQuery()) {
                var t = new EditMessageReplyMarkup();
                t.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
                t.setChatId(update.getCallbackQuery().getFrom().getId().toString());
                //AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery();
                //answerCallbackQuery.setCallbackQueryId(update.getCallbackQuery().getId());
                //answerCallbackQuery.setShowAlert(true);
                //answerCallbackQuery.setText("лох");
                return t;
            }
            return outMessage;
        } catch (Exception e) {
            //todo удалить перед продом
            SendMessage sendMessage = new SendMessage()
                    .setChatId(chatId)
                    .setText(e.getMessage());
            return sendMessage;
        }
    }
}
