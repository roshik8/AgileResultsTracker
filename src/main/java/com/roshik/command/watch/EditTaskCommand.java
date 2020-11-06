package com.roshik.command.watch;

import com.roshik.bot.AgileResultsBot;
import com.roshik.command.*;
import com.roshik.command.create.Storage;
import com.roshik.domains.Task;
import com.roshik.domains.TaskStatus;
import com.roshik.services.KeyBoardService;
import com.roshik.services.TaskService;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;

import java.util.Map;

@ComponentScan
@Service
@Scope(value = "prototype")
public class EditTaskCommand implements ICommand, ICommandValidator, IHasNextCommand, IHasCallbackAnswer {
    private final KeyBoardService keyBoardService;
    private final Storage storage;
    private final TaskService taskService;
    private final AgileResultsBot agileResultsBot;
    private Long currentChatId;
    private String currentMessage;
    private final Map<String, Class<?>> menu = Map.of(
            "Главное меню", MainMenuCommand.class
    );
    private Class<?> NextCommandHandlerName;

    public EditTaskCommand(KeyBoardService keyBoardService, Storage storage, TaskService taskService, AgileResultsBot agileResultsBot) {
        this.keyBoardService = keyBoardService;
        this.storage = storage;
        this.taskService = taskService;
        this.agileResultsBot = agileResultsBot;
    }

    @Override
    public SendMessage generateRequest(Long chatId) {
        currentChatId = chatId;
        ReplyKeyboardMarkup keyboard = keyBoardService.getKeyboard(menu.keySet());
        var message = keyBoardService.createMessage(chatId, "Можешь закрыть задачу или вернуться в главное меню");
        message.setReplyMarkup(keyboard);
        return message;
    }

    @Override
    public void handleResponse(String message) {
        if (menu.containsKey(message)) {
            NextCommandHandlerName = menu.get(message);
        } else {
            NextCommandHandlerName = EditTaskCommand.class;
            currentMessage = message;
            var task = taskService.getTaskById(Long.parseLong(message));
            task.setStatus(TaskStatus.Done);
            taskService.update(task);
        }
    }

    @Override
    public ValidationResult validateMessage(String message, Long user_id) {
        var result = new ValidationResult();
        if (menu.containsKey(message)) {
            result.IsSuccess = true;
            return result;
        }

        if (!tryParseLong(message)) {
            result.IsSuccess = false;
            result.ValidationError = "Неверный формат для редактирования, нажмите галочку";
            return result;
        }

        var canEditTask = taskService.isCanEditTask(user_id, Long.parseLong(message));
        if (!canEditTask) {
            result.IsSuccess = false;
            result.ValidationError = "Нет прав для редактирования задачи";
            return result;
        }

        result.IsSuccess = true;
        return result;
    }

    private boolean tryParseLong(String value) {
        try {
            Long.parseLong(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
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
        var replyMarkup = (InlineKeyboardMarkup) storage.getTempObject(currentChatId);
        for (var row : replyMarkup.getKeyboard()) {
            row.removeIf(button -> button.getCallbackData().equals(currentMessage));
        }

        editMessage.setReplyMarkup(replyMarkup);
        agileResultsBot.sendNewMessage(editMessage);

    }
}
