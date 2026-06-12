package co.com.bancolombia.onboarding.usecase;

import co.com.bancolombia.onboarding.model.user.User;
import co.com.bancolombia.onboarding.model.user.gateways.UserGateway;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@RequiredArgsConstructor
public class GetUsersByNameUseCase {
    private final UserGateway userGateway;

    public Flux<User> getUsersByName(String name) {
        return userGateway.findByName(name);
    }
}
