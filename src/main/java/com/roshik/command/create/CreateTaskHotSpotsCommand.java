package com.roshik.command.create;

import com.roshik.command.*;
import com.roshik.domains.HotSpots;
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
import java.util.*;

import static java.time.temporal.TemporalAdjusters.nextOrSame;
import static java.time.temporal.TemporalAdjusters.previousOrSame;

@ComponentScan
@Service
@Scope(value = "prototype")
public class CreateTaskHotSpotsCommand implements ICommand, ICommandValidator, IHasNextCommand {

    private final Storage storage;
    private final KeyBoardService keyBoardService;
    private final TaskService taskService;
    private Long currentChatId;

    Set<String> menu = Set.of(HotSpots.Life.getTitle(),HotSpots.Work.getTitle(),HotSpots.Personal.getTitle());

    public CreateTaskHotSpotsCommand(Storage storage, KeyBoardService keyBoardService, TaskService taskService) {
        this.storage = storage;
        this.keyBoardService = keyBoardService;
        this.taskService = taskService;
    }

    @Override
    public SendMessage generateRequest(Long chatId) {
        currentChatId = chatId;
        ReplyKeyboardMarkup keyboard = keyBoardService.getKeyboard(new TreeSet<>(menu));
        var message = keyBoardService.createMessage(chatId, "Выбери сферу влияния");
        message.setReplyMarkup(keyboard);
        return message;
    }

    @Override
    public void handleResponse(String message) {
        var task = (Task) storage.getTempObject(currentChatId);
        task.setHotSpots(HotSpots.get(message));
    }


    @Override
    public ValidationResult validateMessage(String message, Long chatId) {
        var result = new ValidationResult();
        if (StringUtils.isEmpty(message)) {
            result.IsSuccess = false;
            result.ValidationError = "Сфера влияния не может быть пустой";
        }
        else if (!menu.contains(message)){
            result.IsSuccess = false;
            result.ValidationError = "Выбери сферу из списка";
        }
        else {
            result.IsSuccess = true;
        }
        return result;
    }

    @Override
    public Class<?> getNextCommandName() {
        return CreateTaskPeriodCommand.class;
    }
}
