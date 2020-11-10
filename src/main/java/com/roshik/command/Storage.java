package com.roshik.command;

import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class Storage {
    private HashMap<Long, Object> storage = new HashMap<>();

    public void saveTempObject(Long chatId, Object data) {
        storage.put(chatId, data);
    }

    public void cleanTempObject(Long chatId) {
        storage.remove(chatId);
    }

    public Object getTempObject(Long currentChatId) {
        return storage.get(currentChatId);
    }
}
