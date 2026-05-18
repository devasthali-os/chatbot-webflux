package com.chatbot.backend.llm.ollama;

import com.chatbot.backend.config.ChatProperties;
import com.chatbot.backend.llm.LlmClient;
import com.chatbot.backend.llm.LlmMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;

@Component
@Profile("!mock-llm")
public class OllamaLlmClient implements LlmClient {

    private static final Logger log = LoggerFactory.getLogger(OllamaLlmClient.class);

    private final WebClient ollamaWebClient;
    private final ChatProperties properties;

    public OllamaLlmClient(WebClient ollamaWebClient, ChatProperties properties) {
        this.ollamaWebClient = ollamaWebClient;
        this.properties = properties;
    }

    @Override
    public Flux<String> streamCompletion(List<LlmMessage> messages, String modelOverride) {
        String model = modelOverride != null && !modelOverride.isBlank()
                ? modelOverride
                : properties.llm().model();
        OllamaChatRequest request = OllamaChatRequest.of(model, messages, properties.llm().maxTokens());

        log.debug("Ollama chat stream model={} messages={}", model, messages.size());

        return ollamaWebClient
                .post()
                .uri("/api/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .bodyToFlux(OllamaStreamLine.class)
                .mapNotNull(line -> {
                    if (line.message() == null || line.message().content() == null) {
                        return null;
                    }
                    String content = line.message().content();
                    return content.isEmpty() ? null : content;
                })
                .filter(Objects::nonNull)
                .onErrorMap(WebClientResponseException.class, ex -> new IllegalStateException(
                        "Ollama error " + ex.getStatusCode() + ": " + ex.getResponseBodyAsString(), ex));
    }

    @Override
    public Mono<Boolean> isAvailable() {
        return ollamaWebClient
                .get()
                .uri("/api/tags")
                .retrieve()
                .toBodilessEntity()
                .map(response -> true)
                .onErrorReturn(false);
    }
}
