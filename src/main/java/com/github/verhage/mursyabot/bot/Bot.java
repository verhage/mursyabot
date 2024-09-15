package com.github.verhage.mursyabot.bot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@Component
public class Bot extends TelegramLongPollingBot {

    private final BotProperties properties;

    public Bot(BotProperties botProperties) {
        super(botProperties.getToken());
        this.properties = botProperties;
        log.info("Bot {} started", botProperties.getName());
    }

    @Override
    public void onUpdateReceived(Update update) {
        log.info("Received: {}", update.getMessage().getText());
        SendMessage sendMessage = SendMessage.builder()
                .chatId(update.getMessage().getChatId())
                .text("Hey, you sent me this: " + update.getMessage().getText())
                .build();
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getBotUsername() {
        return properties.getName();
    }
}
