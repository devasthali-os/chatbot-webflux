package com.chatbot.backend.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

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
                .isEqualTo("chatbot-backend")
                .jsonPath("$.version")
                .isEqualTo("0.1.0-SNAPSHOT");
    }
}
