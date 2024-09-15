package com.github.verhage.mursyabot.bot;

import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.BotSession;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Slf4j
@Component
@RequiredArgsConstructor
public class BotInitializer implements CommandLineRunner {

    private final Bot bot;

    private BotSession session;

    @Override
    public void run(String... args) {
        try {
            TelegramBotsApi api = new TelegramBotsApi(DefaultBotSession.class);
            session = api.registerBot(bot);
        } catch (TelegramApiException e) {
            log.error("An unexpected error occurred: {}", e.getMessage(), e);
        }
    }

    @PreDestroy
    public void onDestroy() {
        if (session != null) {
            session.stop();
        }
    }
}
