package com.roshik.command.watch;

import com.roshik.command.*;
import com.roshik.command.create.Storage;
import com.roshik.domains.Task;
import com.roshik.services.InlineKeyBoardService;
import com.roshik.services.KeyBoardService;
import com.roshik.services.FilterTaskQuery;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ComponentScan
@Service
@Scope(value = "prototype")
public class WatchPermissionCommand implements ICommand, ICommandValidator, IHasNextCommand {
    private final KeyBoardService keyBoardService;
    private final InlineKeyBoardService inlineKeyBoardService;
    private final Storage storage;

    private Long currentChatId;
    private final Map<String, Boolean> menu = Map.of(
            "Свои задачи", true,
            "Чужие задачи", false
    );
    private String NextCommandHandlerName;

    public WatchPermissionCommand(KeyBoardService keyBoardService, InlineKeyBoardService inlineKeyBoardService, Storage storage) {
        this.keyBoardService = keyBoardService;
        this.inlineKeyBoardService = inlineKeyBoardService;
        this.storage = storage;
    }


    @Override
    public SendMessage generateRequest(Long chatId) {
        currentChatId = chatId;
        ReplyKeyboardMarkup keyboard = keyBoardService.getKeyboard(menu.keySet());
        var message = keyBoardService.createMessage(chatId, "Чьи задачи хочешь посмотреть?");
        message.setReplyMarkup(keyboard);
        return message;
    }

    @Override
    public void handleResponse(String message) {
        var selectTaskQuery = new FilterTaskQuery();
        selectTaskQuery.setOwnTasks(menu.get(message));
        storage.saveTempObject(currentChatId, selectTaskQuery);
    }


    @Override
    public ValidationResult validateMessage(String message, Long chatId) {

        var result = new ValidationResult();
        if (StringUtils.isEmpty(message) || !menu.containsKey(message)) {
            result.IsSuccess = false;
            result.ValidationError = "Выбери команду из списка";
        } else {
            result.IsSuccess = true;
        }
        return result;
    }

    @Override
    public Class<?> getNextCommandName() {
        return WatchTaskCommand.class;
    }
}
