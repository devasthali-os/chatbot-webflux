package com.chatbot.backend.config;

import io.netty.channel.ChannelOption;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

@Configuration
@Profile("!mock-llm")
public class OllamaWebClientConfig {

    @Bean
    WebClient ollamaWebClient(ChatProperties properties) {
        ChatProperties.Llm llm = properties.llm();
        HttpClient httpClient = HttpClient.create()
                .responseTimeout(llm.readTimeout())
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, (int) llm.connectTimeout().toMillis());

        return WebClient.builder()
                .baseUrl(llm.baseUrl())
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }
}
