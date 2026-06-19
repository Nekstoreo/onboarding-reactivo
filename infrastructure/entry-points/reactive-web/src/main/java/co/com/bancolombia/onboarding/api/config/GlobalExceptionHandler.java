package co.com.bancolombia.onboarding.api.config;

import co.com.bancolombia.onboarding.model.user.ApiKeyException;
import co.com.bancolombia.onboarding.model.user.UserNotFoundException;
import co.com.bancolombia.onboarding.model.user.ValidationException;
import lombok.extern.log4j.Log4j2;
import org.jspecify.annotations.NullMarked;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Component
@Order(-2)
@Log4j2
@NullMarked
public class GlobalExceptionHandler implements WebExceptionHandler {

    private static final String JSON_MESSAGE_PREFIX = "{\"message\":\"";
    private static final String JSON_REQUEST_ID_INJECT = "\",\"requestId\":\"";
    private static final String JSON_SUFFIX = "\"}";

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        String requestId = exchange.getRequest().getId();
        switch (ex) {
            case UserNotFoundException userNotFoundException -> {
                exchange.getResponse().setStatusCode(HttpStatus.NOT_FOUND);
                exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
                String body = JSON_MESSAGE_PREFIX + userNotFoundException.getMessage() + JSON_REQUEST_ID_INJECT + requestId + JSON_SUFFIX;
                DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8));
                return exchange.getResponse().writeWith(Mono.just(buffer));
            }
            case ValidationException validationException -> {
                exchange.getResponse().setStatusCode(HttpStatus.BAD_REQUEST);
                exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
                String body = JSON_MESSAGE_PREFIX + validationException.getMessage() + JSON_REQUEST_ID_INJECT + requestId + JSON_SUFFIX;
                DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8));
                return exchange.getResponse().writeWith(Mono.just(buffer));
            }
            case ResponseStatusException rse -> {
                exchange.getResponse().setStatusCode(rse.getStatusCode());
                exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
                String reason = rse.getReason() != null ? rse.getReason() : rse.getMessage();
                String body = JSON_MESSAGE_PREFIX + reason + JSON_REQUEST_ID_INJECT + requestId + JSON_SUFFIX;
                DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8));
                return exchange.getResponse().writeWith(Mono.just(buffer));
            }
            case ApiKeyException apiKeyException -> {
                exchange.getResponse().setStatusCode(HttpStatus.BAD_GATEWAY);
                exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
                String body = JSON_MESSAGE_PREFIX + apiKeyException.getMessage() + JSON_REQUEST_ID_INJECT + requestId + JSON_SUFFIX;
                DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8));
                return exchange.getResponse().writeWith(Mono.just(buffer));
            }
            default -> {
                // Continue flow to handle as an unhandled 500 internal server error
            }
        }
        log.error("Unhandled exception occurred for request {}: {}", requestId, ex.getMessage(), ex);
        exchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        String body = JSON_MESSAGE_PREFIX + "Internal Server Error" + JSON_REQUEST_ID_INJECT + requestId + JSON_SUFFIX;
        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8));
        return exchange.getResponse().writeWith(Mono.just(buffer));
    }
}
