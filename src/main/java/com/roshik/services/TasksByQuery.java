package com.roshik.services;

import com.roshik.bot.AgileResultsBot;
import com.roshik.command.EditTaskCommand;
import com.roshik.domains.Task;
import com.roshik.domains.TaskStatus;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Scope(value = "prototype")
public class TasksByQuery {

    private final TaskService taskService;
    private final InlineKeyBoardService inlineKeyBoardService;
    private Class<?> NextCommandHandlerName;
    private final AgileResultsBot agileResultsBot;

    public TasksByQuery(TaskService taskService, InlineKeyBoardService inlineKeyBoardService, AgileResultsBot agileResultsBot) {
        this.taskService = taskService;
        this.inlineKeyBoardService = inlineKeyBoardService;
        this.agileResultsBot = agileResultsBot;
    }


    public void sendTasksByQuery(FilterTaskQuery selectTaskQuery, Long chatId){
        var taskLists = taskService.getTasksByFilterTaskQuery(selectTaskQuery, chatId);

        if (selectTaskQuery.isOwnTasks()) {
            SendMessage sendMessage = new SendMessage()
                    .setChatId(chatId)
                    .setText("Твои задачи");
            agileResultsBot.sendNewMessage(sendMessage);

            var canClose = selectTaskQuery.getStatus() == TaskStatus.Created;
            if (canClose)
                NextCommandHandlerName = EditTaskCommand.class;

            for (var task : taskLists) {
                var taskDescription = "<b>"+task.getHotSpots().getTitle()+":</b> "+task.getName() + System.lineSeparator() +  " по " + task.getPeriod().getStringEnd_date();
                sendMessage = new SendMessage()
                        .setChatId(chatId)
                        .enableHtml(true)
                        .setText(taskDescription);
                if(canClose) {
                    var inlineKeyboardMarkup = new InlineKeyboardMarkup();
                    List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
                    List<InlineKeyboardButton> row = new ArrayList<>();
                    InlineKeyboardButton button = new InlineKeyboardButton();
                    button.setText("Закрыть ✅");
                    button.setCallbackData(task.getId().toString());
                    row.add(button);
                    rowList.add(row);
                    inlineKeyboardMarkup.setKeyboard(rowList);

                    sendMessage.enableHtml(true).setReplyMarkup(inlineKeyboardMarkup);
                }
                agileResultsBot.sendNewMessage(sendMessage);
            }
        } else {
            Map<Long, List<Task>> tasksByUsers = taskLists.stream()
                    .collect(Collectors.groupingBy(Task::getUser_id));
            for (var tasks : tasksByUsers.entrySet()) {
                var userLink = agileResultsBot.getUserLink(tasks.getKey());
                SendMessage sendMessage = new SendMessage()
                        .setChatId(chatId)
                        .setText(userLink);
                sendMessage.enableHtml(true);
                agileResultsBot.sendNewMessage(sendMessage);
                var builder = new StringBuilder();
                for (var task : tasks.getValue()) {
                    builder.append("<b>"+task.getHotSpots().getTitle()+":</b> "+task.getName()).append(" по ").append(task.getPeriod().getStringEnd_date()).append(System.lineSeparator());
                }
                sendMessage = new SendMessage()
                        .setChatId(chatId)
                        .enableHtml(true)
                        .setText(builder.toString());
                agileResultsBot.sendNewMessage(sendMessage);
            }
        }


    }

    public Class<?> getNextCommandHandlerName() {
        return NextCommandHandlerName;
    }
}
