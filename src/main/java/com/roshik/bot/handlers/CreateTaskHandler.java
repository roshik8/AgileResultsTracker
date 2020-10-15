package com.roshik.bot.handlers;

import com.roshik.bot.BotState;
import com.roshik.bot.InputMessageHandler;
import com.roshik.cache.UserDataCache;
import com.roshik.services.ReplyMessagesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;

@Slf4j
@Component
public class CreateTaskHandler implements InputMessageHandler {
    private UserDataCache userDataCache;
    private ReplyMessagesService messagesService;

    public CreateTaskHandler(UserDataCache userDataCache,ReplyMessagesService messagesService) {
        this.userDataCache = userDataCache;
        this.messagesService = messagesService;
    }

    @Override
    public SendMessage handle(Message message) {
        return processUsersInput(message);
    }

    @Override
    public BotState getHandlerName() {
        return BotState.CREATE_TASK;
    }

    private SendMessage processUsersInput(Message inputMsg) {
        int userId = inputMsg.getFrom().getId();
        long chatId = inputMsg.getChatId();

        SendMessage replyToUser = messagesService.getReplyMessage(chatId,"reply.askTaskName");;
        userDataCache.setUsersCurrentBotState(userId,BotState.ASK_TASK_NAME);

        return replyToUser;
    }

}
