package co.com.bancolombia.onboarding.api.config;

import co.com.bancolombia.onboarding.model.user.UserNotFoundException;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Component
@Order(-2)
public class GlobalExceptionHandler implements WebExceptionHandler {

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        if (ex instanceof UserNotFoundException) {
            exchange.getResponse().setStatusCode(HttpStatus.NOT_FOUND);
            exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
            String body = "{\"message\":\"" + ex.getMessage() + "\"}";
            DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8));
            return exchange.getResponse().writeWith(Mono.just(buffer));
        }
        return Mono.error(ex);
    }
}
