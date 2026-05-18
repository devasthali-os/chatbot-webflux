package com.chatbot.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.Executors;

@SpringBootApplication
public class ChatbotApplication {

    public static final int IO_THREADS = Runtime.getRuntime().availableProcessors() * 2;

    @Bean
    Scheduler ioScheduler() {
        return Schedulers.fromExecutor(Executors.newFixedThreadPool(IO_THREADS));
    }

    public static void main(String[] args) {
        SpringApplication.run(ChatbotApplication.class, args);
    }
}
