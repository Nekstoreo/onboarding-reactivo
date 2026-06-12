package co.com.bancolombia.onboarding.api.config;

import co.com.bancolombia.onboarding.model.user.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.http.server.reactive.MockServerHttpResponse;
import org.springframework.mock.web.server.MockServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    void shouldHandleUserNotFoundExceptionAndReturn404() {
        // Arrange
        MockServerHttpRequest request = MockServerHttpRequest.get("/api/users/1").build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);
        UserNotFoundException ex = new UserNotFoundException("User not found");

        // Act
        Mono<Void> result = handler.handle(exchange, ex);

        // Assert
        StepVerifier.create(result)
                .verifyComplete();

        MockServerHttpResponse response = exchange.getResponse();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
    }

    @Test
    void shouldPropagateOtherExceptions() {
        // Arrange
        MockServerHttpRequest request = MockServerHttpRequest.get("/api/users/1").build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);
        RuntimeException ex = new RuntimeException("Unexpected error");

        // Act
        Mono<Void> result = handler.handle(exchange, ex);

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(t -> t.getMessage().equals("Unexpected error"))
                .verify();
    }
}
