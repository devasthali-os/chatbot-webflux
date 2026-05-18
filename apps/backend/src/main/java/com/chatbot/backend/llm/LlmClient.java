package com.chatbot.backend.llm;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface LlmClient {

    Flux<String> streamCompletion(List<LlmMessage> messages, String modelOverride);

    Mono<Boolean> isAvailable();
}
