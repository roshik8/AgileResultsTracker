package com.roshik.command.watch;

import com.roshik.bot.AgileResultsBot;
import com.roshik.command.*;
import com.roshik.command.create.Storage;
import com.roshik.domains.Task;
import com.roshik.domains.TaskStatus;
import com.roshik.services.FilterTaskQuery;
import com.roshik.services.InlineKeyBoardService;
import com.roshik.services.KeyBoardService;
import com.roshik.services.TaskService;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ComponentScan
@Service
@Scope(value = "prototype")
public class WatchPeriodCommand implements ICommand, ICommandValidator, IHasNextCommand {

    private final Map<String, Class<?>> menu = Map.of(
            "За все время", MainMenuCommand.class,
            "Установить период", WatchStartPeriodCommand.class
    );
    private Class<?> NextCommandHandlerName;
    private final KeyBoardService keyBoardService;
    private final TaskService taskService;
    private final InlineKeyBoardService inlineKeyBoardService;
    private final Storage storage;
    private final AgileResultsBot agileResultsBot;
    private Long currentChatId;

    public WatchPeriodCommand(KeyBoardService keyBoardService, TaskService taskService, InlineKeyBoardService inlineKeyBoardService, Storage storage, AgileResultsBot agileResultsBot) {
        this.keyBoardService = keyBoardService;
        this.taskService = taskService;
        this.inlineKeyBoardService = inlineKeyBoardService;
        this.storage = storage;
        this.agileResultsBot = agileResultsBot;
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
        if(message.equals("Установить период")) {
            NextCommandHandlerName = WatchStartPeriodCommand.class;
        }
        else {
            var selectTaskQuery = (FilterTaskQuery) storage.getTempObject(currentChatId);
            storage.saveTempObject(currentChatId, selectTaskQuery);
            var taskLists = taskService.getTasksByFilterTaskQuery(selectTaskQuery, currentChatId);

            if (selectTaskQuery.isOwnTasks()) {
                var sendMessage = inlineKeyBoardService.createMessage(currentChatId, "Твои задачи");
                var canClose = selectTaskQuery.getStatus() == TaskStatus.Created;
                if (canClose)
                    NextCommandHandlerName = EditTaskCommand.class;
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
        if (!menu.containsKey(message)) {
            result.IsSuccess = false;
            result.ValidationError = "Такой команды нет";
        } else {
            result.IsSuccess = true;
        }
        return result;
    }

    @Override
    public Class<?> getNextCommandName() {
        return NextCommandHandlerName;
    }
}
