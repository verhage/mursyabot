package com.github.verhage.mursyabot.bot;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

@Getter
@RequiredArgsConstructor(onConstructor_ = {@ConstructorBinding})
@ConfigurationProperties(prefix = "bot")
public class BotProperties {
    private final String name;

    private final String token;
}
