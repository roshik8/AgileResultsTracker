package com.roshik.command.watch;

import com.roshik.bot.AgileResultsBot;
import com.roshik.command.ICommand;
import com.roshik.command.ICommandValidator;
import com.roshik.command.IHasNextCommand;
import com.roshik.command.ValidationResult;
import com.roshik.command.create.Storage;
import com.roshik.domains.Task;
import com.roshik.domains.TaskStatus;
import com.roshik.services.FilterTaskQuery;
import com.roshik.services.InlineKeyBoardService;
import com.roshik.services.TaskService;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ComponentScan
@Service
@Scope(value = "prototype")
public class WatchEndPeriodCommand implements ICommand, ICommandValidator, IHasNextCommand {
    private final Storage storage;
    private Long currentChatId;
    private final TaskService taskService;
    private final AgileResultsBot agileResultsBot;
    private final InlineKeyBoardService inlineKeyBoardService;
    private Class<?> NextCommandHandlerName;

    public WatchEndPeriodCommand(Storage storage, TaskService taskService, AgileResultsBot agileResultsBot, InlineKeyBoardService inlineKeyBoardService) {
        this.storage = storage;
        this.taskService = taskService;
        this.agileResultsBot = agileResultsBot;
        this.inlineKeyBoardService = inlineKeyBoardService;
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
        var taskLists = taskService.getTasksByFilterTaskQuery(selectTaskQuery, currentChatId);

        if (selectTaskQuery.isOwnTasks()) {
            var sendMessage = inlineKeyBoardService.createMessage(currentChatId, "Твои задачи");
            var canClose = selectTaskQuery.getStatus() == TaskStatus.Created;
            if(canClose)
                NextCommandHandlerName=EditTaskCommand.class;
            InlineKeyboardMarkup replyMarkup = taskToInlineKeyboard(taskLists, canClose);
            storage.saveTempObject(currentChatId, replyMarkup);
            sendMessage.setReplyMarkup(replyMarkup);
            agileResultsBot.sendNewMessage(sendMessage);
        } else {
            Map<Long, List<Task>> tasksByUsers = taskLists.stream()
                    .collect(Collectors.groupingBy(Task::getUser_id));
            for (var tasks : tasksByUsers.entrySet()) {
                var userLink = agileResultsBot.getUserLink(tasks.getKey());
                var sendMessage = inlineKeyBoardService.createMessage(currentChatId, userLink);
                sendMessage.enableHtml(true);
                InlineKeyboardMarkup replyMarkup = taskToInlineKeyboard(tasks.getValue(), false);
                storage.saveTempObject(currentChatId, replyMarkup);
                sendMessage.setReplyMarkup(replyMarkup);
                agileResultsBot.sendNewMessage(sendMessage);
            }
        }
    }

    private InlineKeyboardMarkup taskToInlineKeyboard(List<Task> tasks, boolean canClose) {
        ArrayList<Map<String, String>> rows = new ArrayList<>();
        for (var task : tasks) {
            Map<String, String> buttons = new HashMap<>();
            buttons.put(task.getName() + System.lineSeparator() +  " до " + task.getPeriod().getStringEnd_date(), "-");
            if (canClose)
                buttons.put("✅️", task.getId().toString());
            rows.add(buttons);
        }
        return inlineKeyBoardService.getKeyboard(rows);
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
