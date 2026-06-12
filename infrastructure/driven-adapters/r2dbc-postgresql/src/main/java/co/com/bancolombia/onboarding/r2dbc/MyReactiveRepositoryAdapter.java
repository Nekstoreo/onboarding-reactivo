package co.com.bancolombia.onboarding.r2dbc;

import co.com.bancolombia.onboarding.model.user.User;
import co.com.bancolombia.onboarding.model.user.gateways.UserGateway;
import co.com.bancolombia.onboarding.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public class MyReactiveRepositoryAdapter extends ReactiveAdapterOperations<User, UserEntity, String, MyReactiveRepository>
    implements UserGateway {

    public MyReactiveRepositoryAdapter(MyReactiveRepository repository, ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.map(d, User.class));
    }

    @Override
    public Flux<User> findByName(String name) {
        return repository.findAllByFirstNameIgnoreCase(name)
                .map(this::toEntity);
    }
}
