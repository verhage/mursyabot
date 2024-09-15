package com.github.verhage.mursyabot.bot.engine;

import com.github.verhage.mursyabot.bot.config.BotConfig;
import com.github.verhage.mursyabot.bot.model.Event;
import com.github.verhage.mursyabot.bot.model.State;
import com.github.verhage.mursyabot.bot.model.Transition;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    public List<String> handleMessage(@NonNull final Message message) {
        final Chat chat = chats.computeIfAbsent(message.getChatId(), this::newChat);
        final Event event = new Event(message.getText());

        List<String> responses = new ArrayList<>();
        processEvent(chat, event).ifPresent(responses::add);
        if (responses.isEmpty()) {
            log.info("Event processing error, null response reached");
            responses.add("I don't know what you mean, let's start over");
            responses.add(resetChat(chat));
        }
        return responses;
    }

    private Optional<String> processEvent(Chat chat, Event event) {
        final State toState = findTransition(chat.getCurrentState(), event)
                .map(Transition::toState)
                .map(stateMap::get).orElse(null);

        String response = toState != null ? chat.enter(toState).message() : chat.getCurrentState().message();
        return Optional.ofNullable(response);
    }

    private Optional<Transition> findTransition(State currentState, Event event) {
        return Optional.ofNullable(stateTransitionsMap.get(currentState)).map(m -> m.get(event));
    }

    private Chat newChat(Long id) {
        log.info("Starting new chat");
        Chat chat = new Chat(id);
        chat.enter(NULL_STATE);
        return chat;
    }

    private String resetChat(Chat chat) {
        log.info("Resetting chat to initial state");
        chat.enter(NULL_STATE);
        return processEvent(chat, START).orElseThrow();
    }
}
