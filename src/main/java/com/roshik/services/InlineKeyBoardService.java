package com.roshik.services;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class InlineKeyBoardService {

    public InlineKeyboardMarkup getKeyboard(Map<String, String> textButtons) {
        var inlineKeyboardMarkup = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        for (var text : textButtons.entrySet()) {
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(text.getKey());
            button.setCallbackData(text.getValue());
            keyboardButtonsRow1.add(button);
        }
        rowList.add(keyboardButtonsRow1);
        inlineKeyboardMarkup.setKeyboard(rowList);
        return inlineKeyboardMarkup;
    }

    public InlineKeyboardMarkup getKeyboard(ArrayList<Map<String, String>> textButtons) {
        var inlineKeyboardMarkup = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        for (var row : textButtons) {
            List<InlineKeyboardButton> keyboardButtonsRow = new ArrayList<>();
            for (var text : row.entrySet()) {
                InlineKeyboardButton button = new InlineKeyboardButton();
                button.setText(text.getKey());
                button.setCallbackData(text.getValue());
                keyboardButtonsRow.add(button);
            }
            rowList.add(keyboardButtonsRow);
        }
        inlineKeyboardMarkup.setKeyboard(rowList);
        return inlineKeyboardMarkup;
    }

    public SendMessage createMessage(final long chatId, String textMessage) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(chatId);
        sendMessage.setText(textMessage);
        return sendMessage;
    }
}
