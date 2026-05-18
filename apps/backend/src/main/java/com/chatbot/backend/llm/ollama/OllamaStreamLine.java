package com.chatbot.backend.llm.ollama;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OllamaStreamLine(Message message, boolean done) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Message(String role, String content) {}
}
