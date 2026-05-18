package com.chatbot.backend.api;

import com.chatbot.backend.api.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    Mono<ResponseEntity<ErrorResponse>> handleStatus(ResponseStatusException ex) {
        String error = ex.getStatusCode().value() == 503 ? "LLM_UNAVAILABLE" : ex.getStatusCode().toString();
        return Mono.just(ResponseEntity.status(ex.getStatusCode())
                .body(new ErrorResponse(error, ex.getReason())));
    }

    @ExceptionHandler(WebExchangeBindException.class)
    Mono<ResponseEntity<ErrorResponse>> handleValidation(WebExchangeBindException ex) {
        return Mono.just(ResponseEntity.badRequest()
                .body(new ErrorResponse("VALIDATION_ERROR", "message is required")));
    }
}
