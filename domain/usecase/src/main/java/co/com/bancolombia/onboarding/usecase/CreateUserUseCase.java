package co.com.bancolombia.onboarding.usecase;

import co.com.bancolombia.onboarding.model.user.User;
import co.com.bancolombia.onboarding.model.user.gateways.ExternalUserGateway;
import co.com.bancolombia.onboarding.model.user.gateways.UserEventGateway;
import co.com.bancolombia.onboarding.model.user.gateways.UserGateway;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class CreateUserUseCase {
    private final UserGateway userGateway;
    private final ExternalUserGateway externalUserGateway;
    private final UserEventGateway userEventGateway;

    public Mono<User> createUser(String id) {
        return userGateway.findById(id)
                .switchIfEmpty(Mono.defer(() -> externalUserGateway.fetchUser(id)
                        .flatMap(userGateway::save)
                        .flatMap(savedUser -> userEventGateway.publishUserCreated(savedUser)
                                .thenReturn(savedUser))
                ));
    }
}
