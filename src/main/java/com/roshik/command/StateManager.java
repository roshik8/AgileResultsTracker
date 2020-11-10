package com.roshik.command;

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

    private final static String CANCEL_COMMAND =  "/cancel";

    private HashMap<Long, ICommand> userCommandCache = new HashMap<>();

    public SendMessage handleUpdate(Long chatId, Integer messageId, String message) {
        ICommand nextCommand = null;
        if (!message.equals(CANCEL_COMMAND) && userCommandCache.containsKey(chatId)) {
                var currentCommand = userCommandCache.get(chatId);
                if (currentCommand instanceof ICommandValidator) {

                    var validationResult = ((ICommandValidator) currentCommand).validateMessage(message, chatId);
                    if (!validationResult.IsSuccess) {
                        SendMessage sendMessage = new SendMessage()
                                .setChatId(chatId)
                                .setText(validationResult.ValidationError);
                        return sendMessage;
                    }
                }

                currentCommand.handleResponse(message);

                if (currentCommand instanceof IHasCallbackAnswer) {
                    ((IHasCallbackAnswer) currentCommand).editCallback(messageId);
                }

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
