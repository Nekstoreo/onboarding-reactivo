package co.com.bancolombia.onboarding.consumer;

import co.com.bancolombia.onboarding.model.user.User;
import co.com.bancolombia.onboarding.model.user.gateways.ExternalUserGateway;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class RestConsumer implements ExternalUserGateway {
    private final WebClient client;

    @Override
    @CircuitBreaker(name = "reqresCircuitBreaker")
    public Mono<User> fetchUser(String id) {
        return client.get()
                .uri("/api/users/{id}", id)
                .retrieve()
                .bodyToMono(ReqResUserResponse.class)
                .map(response -> User.builder()
                        .id(response.getData().getId())
                        .email(response.getData().getEmail())
                        .firstName(response.getData().getFirstName())
                        .lastName(response.getData().getLastName())
                        .avatar(response.getData().getAvatar())
                        .build()
                );
    }
}
