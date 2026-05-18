package com.chatbot.backend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.util.List;

@ConfigurationProperties(prefix = "chat")
public record ChatProperties(
        Cors cors,
        Llm llm,
        int maxConcurrentStreams,
        String systemPrompt) {

    public record Cors(List<String> allowedOrigins) {}

    public record Llm(
            String provider,
            String baseUrl,
            String model,
            int maxTokens,
            Duration connectTimeout,
            Duration readTimeout) {}
}
