package com.chatbot.backend.service;

import com.chatbot.backend.llm.LlmMessage;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ConversationStore {

    private final Map<String, List<LlmMessage>> conversations = new ConcurrentHashMap<>();

    public List<LlmMessage> getHistory(String conversationId) {
        if (conversationId == null || conversationId.isBlank()) {
            return List.of();
        }
        return Collections.unmodifiableList(
                conversations.getOrDefault(conversationId, List.of()));
    }

    public void append(String conversationId, LlmMessage message) {
        if (conversationId == null || conversationId.isBlank()) {
            return;
        }
        conversations.compute(conversationId, (id, history) -> {
            List<LlmMessage> updated = history == null ? new ArrayList<>() : new ArrayList<>(history);
            updated.add(message);
            return updated;
        });
    }

    public void clear() {
        conversations.clear();
    }
}
