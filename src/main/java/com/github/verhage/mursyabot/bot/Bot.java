package com.github.verhage.mursyabot.bot;

import com.github.verhage.mursyabot.bot.config.BotConfig;
import com.github.verhage.mursyabot.bot.engine.BotEngine;
import lombok.extern.slf4j.Slf4j;
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
        log.info("Received: {}", update.getMessage().getText());

        String response = engine.handleMessage(update.getMessage());
        reply(update.getMessage(), response);
    }

    @Override
    public String getBotUsername() {
        return properties.getName();
    }

    private void reply(Message message, String response) {
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
}
