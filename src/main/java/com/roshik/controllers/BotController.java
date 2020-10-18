package com.roshik.controllers;

import com.roshik.AgileResultsBot;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.api.methods.BotApiMethod;
import org.telegram.telegrambots.api.objects.Update;

@RestController
public class BotController {
    private final AgileResultsBot agileResultsBot;

    public BotController(AgileResultsBot agileResultsBot){
        this.agileResultsBot=agileResultsBot;
    }

    @PostMapping
    public BotApiMethod<?> onUpdateReceived(@RequestBody Update update) {
        return agileResultsBot.onWebhookUpdateReceived(update);
    }

}
