package com.roshik.config;

import com.roshik.bot.AgileResultsBot;
import com.roshik.bot.TelegramFacade;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.ApiContext;
import org.telegram.telegrambots.bots.DefaultBotOptions;

@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "telegrambot")
public class BotConfig {
    private String webHookPath;
    private String botUserName;
    private String botToken;

    @Bean
    public AgileResultsBot agileResultsBot(TelegramFacade telegramFacade) {
        DefaultBotOptions options = ApiContext.getInstance(DefaultBotOptions.class);
        AgileResultsBot agileResultsBot = new AgileResultsBot(options,telegramFacade);
        agileResultsBot.setBotUserName(botUserName);
        agileResultsBot.setBotToken(botToken);
        agileResultsBot.setWebHookPath(webHookPath);
        return agileResultsBot;
    }

}
