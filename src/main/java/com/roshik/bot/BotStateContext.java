package com.roshik.bot;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;

import java.util.*;

@Component
public class BotStateContext {
    private Map<BotState, InputMessageHandler> messageHandlers = new HashMap<>();

    public BotStateContext(List<InputMessageHandler> messageHandlers) {
        messageHandlers.forEach(handler -> this.messageHandlers.put(handler.getHandlerName(), handler));
    }

    public SendMessage processInputMessage(BotState currentState, Message message) {
        InputMessageHandler currentMessageHandler = findMessageHandler(currentState);
        return currentMessageHandler.handle(message);
    }

    private InputMessageHandler findMessageHandler(BotState currentState) {
        if (isCreateTaskState(currentState)) {
            return messageHandlers.get(BotState.CREATE_TASK);
        }

        if (isEditTaskState(currentState)) {
            return messageHandlers.get(BotState.WATCH_TASKS);
        }

        return messageHandlers.get(currentState);
    }

    private boolean isCreateTaskState(BotState currentState) {
        switch (currentState) {
            case CREATE_TASK:
            case ASK_TASK_NAME:
            case ASK_TASK_PERIOD:
            case ASK_PERMISSION_TASK_TO_WATCH:
                return true;
            default:
                return false;
        }
    }

    private boolean isEditTaskState(BotState currentState) {
        switch (currentState) {
            case EDIT_TASK:
                return true;
            default:
                return false;
        }
    }
}
