package com.example.update.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class TelegramNotificationService {

    private static final Logger log = LoggerFactory.getLogger(TelegramNotificationService.class);
    private final WebClient webClient;
    private final String botToken;
    private final String chatId;

    public TelegramNotificationService(@Value("${telegram.bot.token}") String botToken,
    @Value("${telegram.bot.chat-id}") String chatId) {
        this.botToken = botToken;
        this.chatId = chatId;
        this.webClient = WebClient.builder()
                .baseUrl("https://api.telegram.org/bot" + botToken)
                .build();
    }

    public void sendMessage(String message) {
        webClient.post()
                .uri("/sendMessage")
                .bodyValue(new NotificationRequest(chatId, message))
                .retrieve()
                .bodyToMono(String.class)
                .subscribe(
                        response -> log.info("Telegram notification sent"),
                        error -> log.error("Failed to send Telegram notification", error)
                );
    }

    private record NotificationRequest(String chat_id, String text) {}
}