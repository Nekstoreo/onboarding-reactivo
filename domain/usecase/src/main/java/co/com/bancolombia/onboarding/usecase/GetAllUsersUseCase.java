package co.com.bancolombia.onboarding.usecase;

import co.com.bancolombia.onboarding.model.user.User;
import co.com.bancolombia.onboarding.model.user.gateways.UserGateway;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@RequiredArgsConstructor
public class GetAllUsersUseCase {
    private final UserGateway userGateway;

    public Flux<User> getAllUsers() {
        return userGateway.findAll();
    }
}
