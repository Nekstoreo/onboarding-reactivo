package co.com.bancolombia.onboarding.model.user.gateways;

import co.com.bancolombia.onboarding.model.user.User;
import reactor.core.publisher.Mono;

public interface UserNoSqlGateway {
    Mono<User> save(User user);
}
