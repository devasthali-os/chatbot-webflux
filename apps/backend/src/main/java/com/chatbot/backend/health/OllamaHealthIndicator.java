package com.chatbot.backend.health;

import com.chatbot.backend.llm.LlmClient;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.ReactiveHealthIndicator;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class OllamaHealthIndicator implements ReactiveHealthIndicator {

    private final LlmClient llmClient;

    public OllamaHealthIndicator(LlmClient llmClient) {
        this.llmClient = llmClient;
    }

    @Override
    public Mono<Health> health() {
        return llmClient
                .isAvailable()
                .map(up -> up
                        ? Health.up().withDetail("ollama", "reachable").build()
                        : Health.down().withDetail("ollama", "unreachable").build());
    }
}
