package com.chatbot.backend.api;

import com.chatbot.backend.api.dto.ChatRequest;
import com.chatbot.backend.api.dto.ChatResponse;
import com.chatbot.backend.api.dto.HealthResponse;
import com.chatbot.backend.api.dto.StatusResponse;
import com.chatbot.backend.service.ChatService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping
public class ChatController {

    private static final Logger log = LoggerFactory.getLogger(ChatController.class);
    private static final String VERSION = "0.1.0-SNAPSHOT";

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @GetMapping(value = "/heartbeat", produces = MediaType.APPLICATION_JSON_VALUE)
    Mono<HealthResponse> heartbeat() {
        return Mono.just(new HealthResponse("chatbot-backend", VERSION));
    }

    @GetMapping(value = "/v1/status", produces = MediaType.APPLICATION_JSON_VALUE)
    Mono<StatusResponse> status() {
        return chatService
                .isLlmAvailable()
                .map(ollamaUp -> new StatusResponse("up", VERSION, ollamaUp, chatService.defaultModel()));
    }

    @PostMapping(value = "/v1/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    Flux<ChatResponse> chat(@Valid @RequestBody ChatRequest request) {
        log.debug("POST /v1/chat conversationId={}", request.conversationId());
        return chatService.streamChat(request);
    }
}
