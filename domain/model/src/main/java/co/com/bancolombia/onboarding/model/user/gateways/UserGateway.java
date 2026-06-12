package co.com.bancolombia.onboarding.model.user.gateways;

import co.com.bancolombia.onboarding.model.user.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserGateway {
    Mono<User> findById(String id);
    Mono<User> save(User user);
    Flux<User> findAll();
    Flux<User> findByName(String name);
}
