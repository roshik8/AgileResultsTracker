package com.roshik.command.create;

import com.roshik.bot.AgileResultsBot;
import com.roshik.command.*;
import com.roshik.services.InlineKeyBoardService;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardRemove;

import java.util.Map;
import java.util.TreeMap;

@ComponentScan
@Service
@Scope(value = "prototype")
public class CreateTaskPermissionCommand implements ICommand, IHasNextCommand, ICommandValidator,IHasCallbackAnswer {
    private final InlineKeyBoardService inlineKeyBoardService;
    private final AgileResultsBot agileResultsBot;
    private Class<?> NextCommandHandlerName;
    private String currentMessage;
    private Long currentChatId;
    private final Map<String, String> buttons = Map.of(
            "Да", "Yes!!",
            "Нет", "No!!"
    );

    public CreateTaskPermissionCommand(InlineKeyBoardService inlineKeyBoardService, AgileResultsBot agileResultsBot) {
        this.inlineKeyBoardService = inlineKeyBoardService;
        this.agileResultsBot = agileResultsBot;
    }

    @Override
    public SendMessage generateRequest(Long chatId) {
        currentChatId = chatId;
        SendMessage sendMessage = new SendMessage().setChatId(currentChatId).setText("Задача создана")
                .setReplyMarkup(new ReplyKeyboardRemove());
        agileResultsBot.sendNewMessage(sendMessage);
        InlineKeyboardMarkup keyboard = inlineKeyBoardService.getKeyboard(new TreeMap<>(buttons));
        var message = inlineKeyBoardService.createMessage(chatId, "Дать кому-то права на просмотр?");
        message.setReplyMarkup(keyboard);
        return message;
    }

    @Override
    public void handleResponse(String message) {
        if(message.equals("Yes!!"))
            NextCommandHandlerName = SetPermissionTaskCommand.class;
        currentMessage = message;
    }

    public ValidationResult validateMessage(String message, Long chatId) {
        var result = new ValidationResult();
        if (StringUtils.isEmpty(message)||!buttons.containsValue(message)) {
            result.IsSuccess = false;
            result.ValidationError = "Нажми Да или Нет";
        }
        else {
            result.IsSuccess = true;
        }
        return result;
    }

    @Override
    public Class<?> getNextCommandName() {
        return NextCommandHandlerName;
    }

    @Override
    public void editCallback(Integer messageId) {
        if (currentMessage == null)
            return;

        var editMessage = new EditMessageReplyMarkup();
        editMessage.setMessageId(messageId);
        editMessage.setChatId(currentChatId);
        agileResultsBot.sendNewMessage(editMessage);
    }
}
