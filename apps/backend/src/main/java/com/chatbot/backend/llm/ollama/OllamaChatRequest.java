package com.chatbot.backend.llm.ollama;

import com.chatbot.backend.llm.LlmMessage;
import java.util.List;
import java.util.Map;

public record OllamaChatRequest(
        String model,
        List<LlmMessage> messages,
        boolean stream,
        Map<String, Object> options) {

    public static OllamaChatRequest of(String model, List<LlmMessage> messages, int maxTokens) {
        return new OllamaChatRequest(
                model,
                messages,
                true,
                Map.of("num_predict", maxTokens));
    }
}
