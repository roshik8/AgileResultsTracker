package com.roshik.command.create;

import com.roshik.command.*;
import com.roshik.domains.Period;
import com.roshik.domains.Task;
import com.roshik.services.KeyBoardService;
import com.roshik.services.TaskService;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

@ComponentScan
@Service
@Scope(value = "prototype")
public class CreateTaskPeriodCommand implements ICommand, ICommandValidator, IHasNextCommand {

    private final Storage storage;
    private final KeyBoardService keyBoardService;
    private final TaskService taskService;
    private Long currentChatId;

    private final Map<String, Class<?>> menu = Map.of(
            "День", Object.class,
            "Неделя", Object.class,
            "Месяц", Object.class
    );

    public CreateTaskPeriodCommand(Storage storage, KeyBoardService keyBoardService, TaskService taskService) {
        this.storage = storage;
        this.keyBoardService = keyBoardService;
        this.taskService = taskService;
    }

    @Override
    public SendMessage generateRequest(Long chatId) {
        currentChatId = chatId;
        ReplyKeyboardMarkup keyboard = keyBoardService.getKeyboard(menu.keySet());
        var message = keyBoardService.createMessage(chatId, "Выбери период");
        message.setReplyMarkup(keyboard);
        return message;
    }

    @Override
    public void handleResponse(String message) {
        var period = new Period();
        period.setStart_date(new Date());
        period.setEnd_date(getEndDate(message));

        var task = (Task) storage.getTempObject(currentChatId);
        task.setPeriod(period);
        taskService.add(task);
    }

    private Date getEndDate(String message) {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        if (message.equalsIgnoreCase("День")) {
            c.add(Calendar.DATE, 1);
        } else if (message.equalsIgnoreCase("Месяц")) {
            c.add(Calendar.DATE, 7);
        } else {
            c.add(Calendar.MONTH, 1);
        }
        return c.getTime();
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
        return CreateTaskPermissionCommand.class;
    }
}
