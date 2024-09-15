package com.github.verhage.mursyabot.bot.model;

public record Transition(Event event, String fromState, String toState) {
}
