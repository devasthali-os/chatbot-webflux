package com.chatbot.backend.api;

import com.chatbot.backend.config.ChatProperties;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Map;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class ChatControllerTest {

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    ChatProperties chatProperties;

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
    void statusAllowsLocalhostAndLoopbackOrigins() {
        assertThat(chatProperties.cors().allowedOriginPatterns())
                .contains("http://127.0.0.1:*");

        webTestClient
                .get()
                .uri("/v1/status")
                .header("Origin", "http://127.0.0.1:8080")
                .exchange()
                .expectStatus()
                .isOk()
                .expectHeader()
                .valueEquals("Access-Control-Allow-Origin", "http://127.0.0.1:8080");

        webTestClient
                .get()
                .uri("/v1/status")
                .header("Origin", "http://127.0.0.1:5175")
                .exchange()
                .expectStatus()
                .isOk()
                .expectHeader()
                .valueEquals("Access-Control-Allow-Origin", "http://127.0.0.1:5175");
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
