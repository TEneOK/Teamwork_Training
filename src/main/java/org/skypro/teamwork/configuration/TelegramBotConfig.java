package org.skypro.teamwork.configuration;

import org.skypro.teamwork.service.TelegramBotService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Configuration
public class TelegramBotConfig {

    private final TelegramBotService telegramBotService;

    public TelegramBotConfig(TelegramBotService telegramBotService) {
        this.telegramBotService = telegramBotService;
    }

    @Bean
    public TelegramBotsApi telegramBotsApi() throws TelegramApiException {
        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        try {
            botsApi.registerBot(telegramBotService);
            System.out.println("✅ Telegram bot successfully registered!");
        } catch (TelegramApiException e) {
            System.out.println("❌ Failed to register Telegram bot: " + e.getMessage());
            e.printStackTrace();
        }
        return botsApi;
    }
}