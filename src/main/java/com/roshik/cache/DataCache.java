package com.roshik.cache;

import com.roshik.bot.BotState;

public interface DataCache {
    void setUsersCurrentBotState(int userId, BotState botState);
    BotState getUsersCurrentBotState(int userId);
}
