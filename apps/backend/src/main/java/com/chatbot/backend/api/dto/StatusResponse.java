package com.chatbot.backend.api.dto;

public record StatusResponse(
        String server,
        String version,
        boolean ollama,
        String model) {}
