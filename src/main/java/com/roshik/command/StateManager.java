package com.roshik.command;

import com.roshik.command.create.Storage;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.api.methods.send.SendMessage;

import java.util.HashMap;

@Component
@Getter
@Setter
public class StateManager {
    @Autowired
    BeanFactory beanFactory;
    @Autowired
    Storage storage;

    private HashMap<Long, ICommand> userCommandCache = new HashMap<>();

    public SendMessage handleUpdate(long chatId, String message) {
        ICommand nextCommand = null;

        if (userCommandCache.containsKey(chatId)) {
            var currentCommand = userCommandCache.get(chatId);
            if (currentCommand instanceof ICommandValidator) {
                var validationResult = ((ICommandValidator) currentCommand).ValidateMessage(message);
                if (!validationResult.IsSuccess) {
                    SendMessage sendMessage = new SendMessage()
                            .setChatId(chatId)
                            .setText(validationResult.ValidationError);
                    return sendMessage;
                }
            }

            currentCommand.handleResponse(message);

            if (currentCommand instanceof IHasNextCommand) {
                var nextCommandName = ((IHasNextCommand) currentCommand).getNextCommandName();

                if (nextCommandName != null)
                    nextCommand = (ICommand) beanFactory.getBean(nextCommandName);
            }
        }

        if (nextCommand == null) {
            nextCommand = beanFactory.getBean(MainMenuCommand.class);
            storage.cleanTempObject(chatId);
        }

        userCommandCache.put(chatId, nextCommand);

        var outMessage = nextCommand.generateRequest(chatId);
        return outMessage;
    }
}
