package co.com.bancolombia.onboarding.redis.template;

import co.com.bancolombia.onboarding.model.user.User;
import co.com.bancolombia.onboarding.model.user.gateways.UserCacheGateway;
import co.com.bancolombia.onboarding.redis.template.helper.ReactiveTemplateAdapterOperations;
import lombok.extern.log4j.Log4j2;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Component
@Log4j2
public class ReactiveRedisTemplateAdapter extends ReactiveTemplateAdapterOperations<User, String, User>
        implements UserCacheGateway {

    public ReactiveRedisTemplateAdapter(ReactiveRedisConnectionFactory connectionFactory, ObjectMapper mapper) {
        super(connectionFactory, mapper, d -> mapper.map(d, User.class));
    }

    @Override
    public Mono<User> findById(String id) {
        return super.findById(id)
                .timeout(Duration.ofMillis(500))
                .onErrorResume(ex -> {
                    log.warn("KV Exception (Redis findById failed) for ID {}: {}", id, ex.getMessage(), ex);
                    return Mono.empty();
                });
    }

    @Override
    public Mono<User> save(User user) {
        return super.save(user.getId(), user)
                .timeout(Duration.ofMillis(500))
                .onErrorResume(ex -> {
                    log.warn("KV Exception (Redis save failed) for ID {}: {}", user.getId(), ex.getMessage(), ex);
                    return Mono.just(user);
                });
    }
}
