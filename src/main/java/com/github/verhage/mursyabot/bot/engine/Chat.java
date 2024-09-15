package com.github.verhage.mursyabot.bot.engine;

import com.github.verhage.mursyabot.bot.model.State;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class Chat {
    private final Long id;

    private State currentState;

    public State enter(@NonNull final State newState) {
        if (currentState != null) {
            currentState.onExit();
        }

        currentState = newState;
        currentState.onEnter();

        return currentState;
    }
}
