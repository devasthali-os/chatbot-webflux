package com.chatbot.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.reactive.config.ResourceHandlerRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;

@Configuration
public class WebConfig implements WebFluxConfigurer {

    private final ChatProperties chatProperties;

    public WebConfig(ChatProperties chatProperties) {
        this.chatProperties = chatProperties;
    }

    @Bean
    CorsWebFilter corsWebFilter() {
        CorsConfiguration config = new CorsConfiguration();
        var configured =
                chatProperties.cors() != null ? chatProperties.cors().allowedOrigins() : null;
        var origins = configured != null && !configured.isEmpty()
                ? configured
                : java.util.List.of(
                        "http://localhost:5173",
                        "http://127.0.0.1:5173",
                        "http://localhost:8080",
                        "http://127.0.0.1:8080");
        config.setAllowedOrigins(origins);
        config.setAllowedMethods(java.util.List.of("GET", "POST", "OPTIONS"));
        config.setAllowedHeaders(java.util.List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsWebFilter(source);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/")
                .resourceChain(true);
    }
}
