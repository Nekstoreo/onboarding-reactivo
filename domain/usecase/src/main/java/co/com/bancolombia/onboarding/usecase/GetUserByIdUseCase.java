package co.com.bancolombia.onboarding.usecase;

import co.com.bancolombia.onboarding.model.user.User;
import co.com.bancolombia.onboarding.model.user.UserNotFoundException;
import co.com.bancolombia.onboarding.model.user.gateways.UserCacheGateway;
import co.com.bancolombia.onboarding.model.user.gateways.UserGateway;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class GetUserByIdUseCase {
    private final UserGateway userGateway;
    private final UserCacheGateway userCacheGateway;

    public Mono<User> getUserById(String id) {
        return userCacheGateway.findById(id)
                .switchIfEmpty(Mono.defer(() -> userGateway.findById(id)
                        .flatMap(user -> userCacheGateway.save(user)
                                .thenReturn(user))
                ))
                .switchIfEmpty(Mono.error(new UserNotFoundException("User with id " + id + " not found")));
    }
}
