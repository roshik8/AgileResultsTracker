package com.roshik.command.create;

import com.roshik.command.*;
import com.roshik.domains.Period;
import com.roshik.domains.Task;
import com.roshik.services.KeyBoardService;
import com.roshik.services.TaskService;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.*;

import static java.time.temporal.TemporalAdjusters.nextOrSame;
import static java.time.temporal.TemporalAdjusters.previousOrSame;

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
        ReplyKeyboardMarkup keyboard = keyBoardService.getKeyboard(new TreeSet<>(menu.keySet()));
        var message = keyBoardService.createMessage(chatId, "Выбери период");
        message.setReplyMarkup(keyboard);
        return message;
    }

    @Override
    public void handleResponse(String message) {
        var period = new Period();
        period.setStart_date(getStartDate(message));
        period.setEnd_date(getEndDate(message));

        var task = (Task) storage.getTempObject(currentChatId);
        task.setPeriod(period);
        taskService.add(task);
    }

    private LocalDate getStartDate(String message){
        LocalDate todaydate = LocalDate.now();
        if (message.equalsIgnoreCase("День")) {
            return todaydate;
        } else if (message.equalsIgnoreCase("Месяц")) {
            return todaydate.withDayOfMonth(1);
        } else {
            return todaydate.with(previousOrSame(DayOfWeek.MONDAY));
        }
    }

    private LocalDate getEndDate(String message) {
        LocalDate todaydate = LocalDate.now();
        if (message.equalsIgnoreCase("День")) {
            return todaydate;
        } else if (message.equalsIgnoreCase("Месяц")) {
            return todaydate.withDayOfMonth(todaydate.lengthOfMonth());
        } else {
            return todaydate.with(nextOrSame(DayOfWeek.SUNDAY));
        }
    }

    @Override
    public ValidationResult validateMessage(String message, Long chatId) {
        var result = new ValidationResult();
        if (StringUtils.isEmpty(message)) {
            result.IsSuccess = false;
            result.ValidationError = "Период не может быть пустым";
        }
        else if (!menu.containsKey(message)){
            result.IsSuccess = false;
            result.ValidationError = "Выбери период из списка";
        }
        else {
            result.IsSuccess = true;
        }
        return result;
    }

    @Override
    public Class<?> getNextCommandName() {
        return CreateTaskPermissionCommand.class;
    }
}
