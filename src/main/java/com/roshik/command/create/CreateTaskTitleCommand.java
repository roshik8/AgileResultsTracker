package com.roshik.command.create;

import com.roshik.command.*;
import com.roshik.domains.Task;
import com.roshik.domains.TaskStatus;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.api.methods.send.SendMessage;

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
                .setText("Введи название задачи");
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
    public ValidationResult ValidateMessage(String message) {
        var result = new ValidationResult();
        if (message == null || message.length() < 1) {
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
