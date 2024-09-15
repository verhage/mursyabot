package com.github.verhage.mursyabot.bot.engine;

import com.github.verhage.mursyabot.bot.config.BotConfig;
import com.github.verhage.mursyabot.bot.model.Event;
import com.github.verhage.mursyabot.bot.model.State;
import com.github.verhage.mursyabot.bot.model.Transition;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
public class BotEngine {
    private static final Event START = new Event("/start");

    private static final State NULL_STATE = new State("NULL", null);

    private final Map<Long, Chat> chats = new HashMap<>();

    private final Map<String, State> stateMap = new HashMap<>();

    private final Map<State, Map<Event, Transition>> stateTransitionsMap = new HashMap<>();

    public BotEngine(@NonNull final BotConfig config) {
        for (State state : config.states()) {
            stateMap.put(state.name(), state);
        }

        stateTransitionsMap.put(NULL_STATE, Map.of(START, new Transition(START, NULL_STATE.name(), config.initialState())));
        for (Transition transition : config.transitions()) {
            stateTransitionsMap.compute(stateMap.get(transition.fromState()), (state, eventTransitionMap) -> {
                if (eventTransitionMap == null) {
                    eventTransitionMap = new HashMap<>();
                }
                eventTransitionMap.put(transition.event(), transition);
                return eventTransitionMap;
            });
        }
    }

    public String handleMessage(@NonNull final Message message) {
        final Chat chat = chats.computeIfAbsent(message.getChatId(), this::newChat);

        final Event event = new Event(message.getText());
        final State toState = findTransition(chat.getCurrentState(), event)
                .map(Transition::toState)
                .map(stateMap::get).orElse(null);

        if (toState != null) {
            return chat.enter(toState).message();
        }

        log.info("Unsupported transition");

        return chat.getCurrentState().message();
    }

    private Optional<Transition> findTransition(State currentState, Event event) {
        return Optional.ofNullable(stateTransitionsMap.get(currentState)).map(m -> m.get(event));
    }

    private Chat newChat(Long id) {
        Chat chat = new Chat(id);
        chat.enter(NULL_STATE);
        return chat;
    }
}
