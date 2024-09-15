package com.github.verhage.mursyabot.bot;

import com.github.verhage.mursyabot.bot.config.BotConfig;
import com.github.verhage.mursyabot.bot.engine.BotEngine;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@Component
public class Bot extends TelegramLongPollingBot {

    private final BotProperties properties;

    private final BotEngine engine;

    public Bot(BotProperties botProperties, BotConfig config) {
        super(botProperties.getToken());
        this.properties = botProperties;
        this.engine = new BotEngine(config);
        log.info("Bot {} started", botProperties.getName());
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            setupMdc(update);
            engine.handleMessage(update.getMessage()).forEach(response -> respond(update.getMessage(), response));
        } finally {
            MDC.clear();
        }
    }

    @Override
    public String getBotUsername() {
        return properties.getName();
    }

    private void respond(Message message, String response) {
        SendMessage sendMessage = SendMessage.builder()
                .chatId(message.getChatId())
                .text(response)
                .build();
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void setupMdc(Update update) {
        MDC.put("bot", properties.getName());
        MDC.put("chatId", String.valueOf(update.getMessage().getChatId()));
    }
}
