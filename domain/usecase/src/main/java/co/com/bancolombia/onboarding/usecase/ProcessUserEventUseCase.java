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
                .firstName(toUpperCase(user.getFirstName()))
                .lastName(toUpperCase(user.getLastName()))
                .email(toUpperCase(user.getEmail()))
                .avatar(toUpperCase(user.getAvatar()))
                .build();

        return userNoSqlGateway.save(uppercaseUser);
    }

    private String toUpperCase(String value) {
        return value != null ? value.toUpperCase() : null;
    }
}
