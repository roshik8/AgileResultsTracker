package com.roshik.command.create;

import com.google.gson.Gson;
import com.roshik.bot.AgileResultsBot;
import com.roshik.command.ICommand;
import com.roshik.command.ICommandValidator;
import com.roshik.command.ValidationResult;
import com.roshik.domains.Permission;
import com.roshik.domains.Task;
import com.roshik.services.PermissionService;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Contact;

@ComponentScan
@Service
@Scope(value = "prototype")
public class SetPermissionTaskCommand implements ICommand, ICommandValidator {
    private Long currentChatId;
    private final Storage storage;
    private final PermissionService permissionService;
    private final AgileResultsBot agileResultsBot;

    public SetPermissionTaskCommand(Storage storage, PermissionService permissionService, AgileResultsBot agileResultsBot) {
        this.storage = storage;
        this.permissionService = permissionService;
        this.agileResultsBot = agileResultsBot;
    }

    @Override
    public SendMessage generateRequest(Long chatId) {
        currentChatId = chatId;
        SendMessage sendMessage = new SendMessage()
                .setChatId(currentChatId)
                .setText("Отправь контакт, кому хочешь дать доступ");
        return sendMessage;
    }

    @Override
    public void handleResponse(String message) {
        Gson gson = new Gson();
        Contact contact = gson.fromJson(message, Contact.class);
        Task task = (Task) storage.getTempObject(currentChatId);
        var permission = new Permission();
        permission.setId_owner(task.getUser_id());
        permission.setTask(task);
        permission.setPermission_owner((long) contact.getUserID());
        permissionService.add(permission);
        agileResultsBot.sendMessage(currentChatId,"Права на просмотр предоставлены");
        var textMessage = "Вам дали доступ на просмотр задачи "+"<b>"+task.getName()+"</b>, пользователь "+agileResultsBot.getUserLink(task.getUser_id());
        var permissionMessage = new SendMessage().setChatId((long) contact.getUserID())
                .setText(textMessage)
                .enableHtml(true);
        agileResultsBot.sendNewMessage(permissionMessage);

    }

    @Override
    public ValidationResult validateMessage(String message, Long chatId) {
        var result = new ValidationResult();
        Gson gson = new Gson();
        try{
            Contact contact = gson.fromJson(message, Contact.class);
            result.IsSuccess = true;
        }
        catch (Exception e){
            result.IsSuccess = false;
            result.ValidationError = "Не является контактом, попробуй еще раз";
        }
        return result;
    }
}
