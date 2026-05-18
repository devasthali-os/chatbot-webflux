package com.chatbot.backend.llm;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;

@Component
@Profile("mock-llm")
public class MockLlmClient implements LlmClient {

    @Override
    public Flux<String> streamCompletion(List<LlmMessage> messages, String modelOverride) {
        String userText = messages.isEmpty()
                ? ""
                : messages.get(messages.size() - 1).content();
        String reply = "Mock reply to: " + userText;
        return Flux.fromArray(reply.split(" "))
                .delayElements(Duration.ofMillis(30))
                .map(word -> word + " ");
    }

    @Override
    public Mono<Boolean> isAvailable() {
        return Mono.just(true);
    }
}
