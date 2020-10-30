package com.roshik.command.create;

import com.roshik.bot.AgileResultsBot;
import com.roshik.command.*;
import com.roshik.services.PermissionMenuService;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.api.methods.send.SendMessage;


@ComponentScan
@Service
@Scope(value = "prototype")
public class CreateTaskPermissionCommand implements ICommand, IHasNextCommand {
    private final PermissionMenuService permissionMenuService;
    private final AgileResultsBot agileResultsBot;
    private Class<?> NextCommandHandlerName;

    public CreateTaskPermissionCommand(PermissionMenuService permissionMenuService, AgileResultsBot agileResultsBot) {
        this.permissionMenuService = permissionMenuService;
        this.agileResultsBot = agileResultsBot;
    }

    @Override
    public SendMessage generateRequest(Long chatId) {
        agileResultsBot.sendMessage(chatId,"Задача создана");
        return permissionMenuService.getPermissionMenuMessage(chatId,
                "Дать кому-то права на просмотр?");
    }

    @Override
    public void handleResponse(String message) {
        if(message.equals("Yes"))
            NextCommandHandlerName = SetPermissionTaskCommand.class;
    }

    public ValidationResult ValidateMessage(String message) {
        return null;
    }

    @Override
    public Class<?> getNextCommandName() {
        return NextCommandHandlerName;
    }
}
