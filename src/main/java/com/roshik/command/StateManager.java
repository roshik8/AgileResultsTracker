package com.roshik.command;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.api.methods.send.SendMessage;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

@Component
@Getter
@Setter
public class StateManager {
    @Autowired
    BeanFactory beanFactory;

    private HashSet<String> commands = new HashSet<>(Arrays.asList("CreateTaskCommand","WatchTaskCommand"));

    private HashMap<Long,ICommand> userCommandCache = new HashMap<>();

    public SendMessage handleUpdate(long chatId, String message) {
        ICommand nextCommand = null;

        if (userCommandCache.containsKey(chatId))
        {
            var currentCommand = userCommandCache.get(chatId);
            if (currentCommand instanceof ICommandValidator)
            {
                var validationResult = ((ICommandValidator) currentCommand).ValidateMessage(message);
                if (!validationResult.IsSuccess)
                {
                    SendMessage sendMessage = new SendMessage()
                            .setChatId(chatId)
                    .setText(validationResult.ValidationError);
                    return sendMessage;
                }
            }

            currentCommand.HandleIncomingMessage(message);

            if (currentCommand instanceof IHasNextCommand)
            {
                var nextCommandName = ((IHasNextCommand) currentCommand).getNextCommandName();
                if (commands.contains(nextCommandName))
                {
                    try {
                        Class<?> aClass = Class.forName("com.roshik.command." + nextCommandName);
                        nextCommand = (ICommand) beanFactory.getBean(aClass);
                    }catch (ClassNotFoundException e)
                    {

                    }

                }
            }
        }

        if (nextCommand == null)
            nextCommand = beanFactory.getBean(MainMenuCommand.class);

        userCommandCache.put(chatId, nextCommand);

        var outMessage = nextCommand.InitOutgoingMessage(chatId);
        return outMessage;
    }
}
