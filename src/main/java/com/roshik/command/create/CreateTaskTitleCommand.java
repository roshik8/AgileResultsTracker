package com.roshik.command.create;

import com.roshik.command.*;
import com.roshik.domains.Task;
import com.roshik.domains.TaskStatus;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardRemove;

@ComponentScan
@Service
@Scope(value = "prototype")
public class CreateTaskTitleCommand implements ICommand, ICommandValidator, IHasNextCommand {

    private final Storage storage;
    private Long currentChatId;

    public CreateTaskTitleCommand(Storage storage) {
        this.storage = storage;
    }

    @Override
    public SendMessage generateRequest(Long chatId) {
        currentChatId = chatId;
        SendMessage sendMessage = new SendMessage()
                .setChatId(currentChatId)
                .setText("Введи название задачи")
                .setReplyMarkup(new ReplyKeyboardRemove());
        return sendMessage;
    }

    @Override
    public void handleResponse(String message) {
        var task = new Task();

        task.setStatus(TaskStatus.Created);
        task.setName(message);
        task.setUser_id(currentChatId);

        storage.saveTempObject(currentChatId, task);
    }

    @Override
    public ValidationResult validateMessage(String message, Long chatId) {
        var result = new ValidationResult();
        if (StringUtils.isEmpty(message)) {
            result.IsSuccess = false;
            result.ValidationError = "Название не может быть пустым";
        } else {
            result.IsSuccess = true;
        }
        return result;
    }

    @Override
    public Class<?> getNextCommandName() {
        return CreateTaskPeriodCommand.class;
    }
}
