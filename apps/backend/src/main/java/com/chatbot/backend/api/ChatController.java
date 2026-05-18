package com.chatbot.backend.api;

import com.chatbot.backend.api.dto.ChatResponse;
import com.chatbot.backend.api.dto.HealthResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.stream.Stream;

@RestController
@RequestMapping
public class ChatController {

    private static final Logger log = LoggerFactory.getLogger(ChatController.class);

    @GetMapping(value = "/heartbeat", produces = MediaType.APPLICATION_JSON_VALUE)
    Mono<HealthResponse> heartbeat() {
        log.debug("GET /heartbeat");
        return Mono.just(new HealthResponse("chatbot-backend", "0.1.0-SNAPSHOT"));
    }

    @GetMapping(value = "/v2/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    Flux<ChatResponse> legacyMockChat() {
        return Flux.fromStream(
                Stream.of(
                        ChatResponse.builder().message("message1: hi how can i help you").build(),
                        ChatResponse.builder().message("message2: Please see the FAQ for more.").build(),
                        ChatResponse.builder()
                                .message("message3: If this does not answer your question. Talk to our rep.")
                                .build()
                )
        ).delayElements(Duration.ofSeconds(1));
    }
}
