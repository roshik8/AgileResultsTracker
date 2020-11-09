package com.roshik.command.watch;

import com.roshik.command.ICommand;
import com.roshik.command.ICommandValidator;
import com.roshik.command.IHasNextCommand;
import com.roshik.command.ValidationResult;
import com.roshik.command.create.Storage;
import com.roshik.services.FilterTaskQuery;
import com.roshik.services.TasksByQuery;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.api.methods.send.SendMessage;

import java.text.ParseException;
import java.text.SimpleDateFormat;

@ComponentScan
@Service
@Scope(value = "prototype")
public class WatchEndPeriodCommand implements ICommand, ICommandValidator, IHasNextCommand {
    private final Storage storage;
    private Long currentChatId;
    private final TasksByQuery tasksByQuery;
    private Class<?> NextCommandHandlerName;

    public WatchEndPeriodCommand(Storage storage, TasksByQuery tasksByQuery) {
        this.storage = storage;
        this.tasksByQuery = tasksByQuery;
    }

    @Override
    public SendMessage generateRequest(Long chatId) {
        currentChatId = chatId;
        SendMessage sendMessage = new SendMessage()
                .setChatId(currentChatId)
                .setText("Введи по какую дату хочешь посмотреть задачи в формате День.Месяц.Год");
        return sendMessage;
    }

    @Override
    public void handleResponse(String message) {
        var selectTaskQuery = (FilterTaskQuery) storage.getTempObject(currentChatId);
        selectTaskQuery.setEnd_date(message);
        storage.saveTempObject(currentChatId, selectTaskQuery);
        tasksByQuery.sendTasksByQuery(selectTaskQuery,currentChatId);
        NextCommandHandlerName=tasksByQuery.getNextCommandHandlerName();
    }


    @Override
    public ValidationResult validateMessage(String message, Long chatId) {

        var result = new ValidationResult();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        dateFormat.setLenient(false);
        try {
            dateFormat.parse(message.trim());
            result.IsSuccess = true;
        } catch (ParseException pe) {
            result.IsSuccess = false;
            result.ValidationError = "Неверный формат даты, введи в формате День.Месяц.Год, например 12.12.2012";
        }
        return result;
    }

    @Override
    public Class<?> getNextCommandName() {
        return NextCommandHandlerName;
    }
}
