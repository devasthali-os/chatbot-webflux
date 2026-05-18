package com.chatbot.backend.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Map;

@SpringBootTest
@AutoConfigureWebTestClient
class ChatControllerTest {

    @Autowired
    WebTestClient webTestClient;

    @Test
    void heartbeatReturnsServiceInfo() {
        webTestClient
                .get()
                .uri("/heartbeat")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$.service")
                .isEqualTo("chatbot-backend");
    }

    @Test
    void statusReportsMockLlmUp() {
        webTestClient
                .get()
                .uri("/v1/status")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$.ollama")
                .isEqualTo(true)
                .jsonPath("$.server")
                .isEqualTo("up");
    }

    @Test
    void chatStreamsMockReply() {
        webTestClient
                .post()
                .uri("/v1/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.TEXT_EVENT_STREAM)
                .bodyValue(Map.of("message", "hello"))
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(String.class)
                .getResponseBody()
                .collectList()
                .block();
    }

    @Test
    void chatRejectsEmptyMessage() {
        webTestClient
                .post()
                .uri("/v1/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("message", ""))
                .exchange()
                .expectStatus()
                .isBadRequest();
    }
}
