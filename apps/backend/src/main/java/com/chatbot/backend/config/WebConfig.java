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
        var cors = chatProperties.cors();
        var patterns = cors != null ? cors.allowedOriginPatterns() : null;
        var origins = cors != null ? cors.allowedOrigins() : null;

        if (patterns != null && !patterns.isEmpty()) {
            config.setAllowedOriginPatterns(patterns);
        } else if (origins != null && !origins.isEmpty()) {
            config.setAllowedOrigins(origins);
        } else {
            config.setAllowedOriginPatterns(java.util.List.of(
                    "http://localhost:*",
                    "http://127.0.0.1:*"));
        }
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
