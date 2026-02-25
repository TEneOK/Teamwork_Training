package org.skypro.teamwork.service;

import org.skypro.teamwork.models.Recommendation;
import org.skypro.teamwork.models.RecommendationsResponse;
import org.skypro.teamwork.models.Users;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

@Service
public class TelegramBotService extends TelegramLongPollingBot {

    private static final Logger logger = LoggerFactory.getLogger(TelegramBotService.class);

    private final RecommendationService recommendationService;
    private final UserService userService;

    @Value("${telegram.bot.username}")
    private String botUsername;

    public TelegramBotService(
            @Value("${telegram.bot.token}") String botToken,
            RecommendationService recommendationService,
            UserService userService) {
        super(botToken);
        this.recommendationService = recommendationService;
        this.userService = userService;
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return;
        }

        long chatId = update.getMessage().getChatId();
        String messageText = update.getMessage().getText();

        try {
            if (messageText.equals("/start")) {
                sendWelcomeMessage(chatId);
            } else if (messageText.startsWith("/recommend ")) {
                handleRecommendCommand(chatId, messageText);
            } else {
                sendHelpMessage(chatId);
            }
        } catch (Exception e) {
            logger.error("Error processing telegram message", e);
            sendErrorMessage(chatId);
        }
    }

    private void sendWelcomeMessage(long chatId) {
        String welcomeText = """
                Добро пожаловать в бот банковских рекомендаций! 🏦
                
                Доступные команды:
                /recommend <имя> - получить рекомендации для пользователя
                /help - показать эту справку
                """;
        sendMessage(chatId, welcomeText);
    }

    private void sendHelpMessage(long chatId) {
        String helpText = """
                Справка по командам:
                
                /recommend <имя> - получить персонализированные рекомендации
                Пример: /recommend Иван Петров
                
                /help - показать эту справку
                """;
        sendMessage(chatId, helpText);
    }

    private void handleRecommendCommand(long chatId, String messageText) {
        String userName = messageText.substring("/recommend ".length()).trim();

        if (userName.isEmpty()) {
            sendMessage(chatId, "Пожалуйста, укажите имя пользователя. Пример: /recommend Иван Петров");
            return;
        }

        List<Users> users = userService.findUsersByName(userName);

        if (users.isEmpty()) {
            sendMessage(chatId, "❌ Пользователь не найден");
            return;
        }

        if (users.size() > 1) {
            sendMessage(chatId, "❌ Найдено несколько пользователей. Уточните имя");
            return;
        }

        Users user = users.get(0);
        RecommendationsResponse recommendations =
                recommendationService.getRecommendationsForUser(user.getId());

        sendRecommendations(chatId, user, recommendations.getRecommendations());
    }

    private void sendRecommendations(long chatId, Users user, List<Recommendation> recommendations) {
        StringBuilder message = new StringBuilder();
        message.append(String.format("Здравствуйте, %s %s 👋\n\n",
                user.getFirstName(), user.getLastName()));

        if (recommendations.isEmpty()) {
            message.append("На данный момент для вас нет новых продуктов 😊");
        } else {
            message.append("Новые продукты для вас:\n\n");
            for (int i = 0; i < recommendations.size(); i++) {
                Recommendation rec = recommendations.get(i);
                message.append(String.format("%d. %s\n   %s\n\n",
                        i + 1, rec.getProductName(), rec.getDescription()));
            }
        }

        sendMessage(chatId, message.toString());
    }

    private void sendErrorMessage(long chatId) {
        sendMessage(chatId, "❌ Произошла ошибка. Пожалуйста, попробуйте позже.");
    }

    private void sendMessage(long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);
        message.setParseMode("HTML");

        try {
            execute(message);
        } catch (TelegramApiException e) {
            logger.error("Error sending telegram message", e);
        }
    }
}