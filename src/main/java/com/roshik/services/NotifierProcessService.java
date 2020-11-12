package com.roshik.services;

import com.roshik.bot.AgileResultsBot;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.api.methods.send.SendMessage;

import java.text.SimpleDateFormat;
import java.util.Date;


@Service
public class NotifierProcessService {
    private final TaskService taskService;
    private final AgileResultsBot agileResultsBot;

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    public NotifierProcessService(TaskService taskService, AgileResultsBot agileResultsBot) {
        this.taskService = taskService;
        this.agileResultsBot = agileResultsBot;
    }

    @Scheduled(cron = "${notifier.processPeriod}")
    public void closeOverdueTasks() {
        System.out.println("Проверка задач");
        var tasks = taskService.updateExpiredTask();

        for(var task: tasks){
            var sendMessage = new SendMessage().setChatId(task.getUser_id())
                    .setText("Задача <b>"+task.getName()+"</b> просрочена")
                    .enableHtml(true);
            agileResultsBot.sendNewMessage(sendMessage);
        }

    }


}
