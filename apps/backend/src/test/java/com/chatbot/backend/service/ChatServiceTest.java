package com.chatbot.backend.service;

import com.chatbot.backend.api.dto.ChatRequest;
import com.chatbot.backend.api.dto.ChatResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.test.StepVerifier;

@SpringBootTest
class ChatServiceTest {

    @Autowired
    ChatService chatService;

    @Autowired
    ConversationStore conversationStore;

    @BeforeEach
    void clear() {
        conversationStore.clear();
    }

    @Test
    void streamsTokensAndCompletes() {
        StepVerifier.create(chatService.streamChat(new ChatRequest("hello", null, null)))
                .thenConsumeWhile(r -> !r.isDone())
                .expectNextMatches(ChatResponse::isDone)
                .verifyComplete();
    }
}
