package com.chatbot.backend.service;

import com.chatbot.backend.api.dto.ChatRequest;
import com.chatbot.backend.api.dto.ChatResponse;
import com.chatbot.backend.config.ChatProperties;
import com.chatbot.backend.llm.LlmClient;
import com.chatbot.backend.llm.LlmMessage;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Semaphore;

@Service
public class ChatService {

    private final LlmClient llmClient;
    private final ConversationStore conversationStore;
    private final ChatProperties properties;
    private final Semaphore streamPermits;

    public ChatService(LlmClient llmClient, ConversationStore conversationStore, ChatProperties properties) {
        this.llmClient = llmClient;
        this.conversationStore = conversationStore;
        this.properties = properties;
        this.streamPermits = new Semaphore(properties.maxConcurrentStreams());
    }

    public Flux<ChatResponse> streamChat(ChatRequest request) {
        if (!streamPermits.tryAcquire()) {
            return Flux.error(new ResponseStatusException(
                    HttpStatus.TOO_MANY_REQUESTS, "Too many concurrent chat streams"));
        }

        String conversationId = request.conversationId() != null && !request.conversationId().isBlank()
                ? request.conversationId()
                : UUID.randomUUID().toString();

        List<LlmMessage> messages = buildMessages(request, conversationId);
        StringBuilder assistantReply = new StringBuilder();

        return llmClient
                .streamCompletion(messages, request.model())
                .map(token -> {
                    assistantReply.append(token);
                    return ChatResponse.builder().message(token).done(false).build();
                })
                .concatWith(Mono.fromCallable(() -> {
                    conversationStore.append(conversationId, new LlmMessage("user", request.message()));
                    conversationStore.append(conversationId, new LlmMessage("assistant", assistantReply.toString()));
                    return ChatResponse.builder().message("").done(true).build();
                }))
                .doFinally(signal -> streamPermits.release())
                .onErrorMap(ex -> ex instanceof ResponseStatusException
                        ? ex
                        : new ResponseStatusException(
                                HttpStatus.BAD_GATEWAY,
                                "LLM unavailable: " + ex.getMessage(),
                                ex));
    }

    public Mono<Boolean> isLlmAvailable() {
        return llmClient.isAvailable();
    }

    public String defaultModel() {
        return properties.llm().model();
    }

    private List<LlmMessage> buildMessages(ChatRequest request, String conversationId) {
        List<LlmMessage> messages = new ArrayList<>();
        messages.add(new LlmMessage("system", properties.systemPrompt()));
        messages.addAll(conversationStore.getHistory(conversationId));
        messages.add(new LlmMessage("user", request.message()));
        return messages;
    }
}
