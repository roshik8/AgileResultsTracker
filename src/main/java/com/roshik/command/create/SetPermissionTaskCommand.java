package com.roshik.command.create;

import com.roshik.command.ICommand;
import com.roshik.command.ICommandValidator;
import com.roshik.command.IHasNextCommand;
import com.roshik.command.ValidationResult;
import com.roshik.command.create.CreateTaskPermissionCommand;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.api.methods.send.SendMessage;

@ComponentScan
@Service
@Scope(value = "prototype")
public class SetPermissionTaskCommand implements ICommand, ICommandValidator, IHasNextCommand {



    @Override
    public Class<?> getNextCommandName() {
        return CreateTaskPermissionCommand.class;
    }

    @Override
    public SendMessage generateRequest(Long chatId) {
        return null;
    }

    @Override
    public void handleResponse(String message) {

    }

    @Override
    public ValidationResult ValidateMessage(String message) {
        return null;
    }
}
