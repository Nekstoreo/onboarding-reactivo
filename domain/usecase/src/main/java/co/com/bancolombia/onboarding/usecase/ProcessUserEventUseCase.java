package co.com.bancolombia.onboarding.usecase;

import co.com.bancolombia.onboarding.model.user.User;
import co.com.bancolombia.onboarding.model.user.gateways.UserNoSqlGateway;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class ProcessUserEventUseCase {
    private final UserNoSqlGateway userNoSqlGateway;

    public Mono<User> processUserEvent(User user) {
        if (user == null) {
            return Mono.empty();
        }
        User uppercaseUser = user.toBuilder()
                .firstName(user.getFirstName() != null ? user.getFirstName().toUpperCase() : null)
                .lastName(user.getLastName() != null ? user.getLastName().toUpperCase() : null)
                .email(user.getEmail() != null ? user.getEmail().toUpperCase() : null)
                .avatar(user.getAvatar() != null ? user.getAvatar().toUpperCase() : null)
                .build();

        return userNoSqlGateway.save(uppercaseUser);
    }
}
