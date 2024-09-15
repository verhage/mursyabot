package com.github.verhage.mursyabot;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.verhage.mursyabot.bot.config.BotConfig;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
@ConfigurationPropertiesScan
public class MursyabotConfig {
    @Bean
    public BotConfig sampleBot(ObjectMapper objectMapper) throws IOException {
        return objectMapper.readValue(getClass().getResourceAsStream("/sample-bot.json"), BotConfig.class);
    }
}
