package com.github.verhage.mursyabot.bot.config;

import com.github.verhage.mursyabot.bot.model.State;
import com.github.verhage.mursyabot.bot.model.Transition;

import java.util.List;

public record BotConfig(List<State> states, List<Transition> transitions, String initialState) {
}
