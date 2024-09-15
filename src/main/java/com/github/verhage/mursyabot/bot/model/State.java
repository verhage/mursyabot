package com.github.verhage.mursyabot.bot.model;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public record State(String name, String message) {
    public void onEnter() {
        log.info("Entering state: {}", name);
    }

    public void onExit() {
        log.info("Exiting state: {}", name);
    }
}
