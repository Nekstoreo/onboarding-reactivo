package co.com.bancolombia.onboarding.r2dbc;

import co.com.bancolombia.onboarding.model.user.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MyReactiveRepositoryAdapterTest {

    @InjectMocks
    private MyReactiveRepositoryAdapter repositoryAdapter;

    @Mock
    private MyReactiveRepository repository;

    @Mock
    private ObjectMapper mapper;

    @Test
    void mustFindValueById() {
        User user = User.builder().id("1").email("test@test.com").build();
        UserEntity entity = UserEntity.builder().id("1").email("test@test.com").build();

        when(repository.findById("1")).thenReturn(Mono.just(entity));
        when(mapper.map(entity, User.class)).thenReturn(user);

        Mono<User> result = repositoryAdapter.findById("1");

        StepVerifier.create(result)
                .expectNext(user)
                .verifyComplete();
    }

    @Test
    void mustSaveValue() {
        User user = User.builder().id("1").email("test@test.com").build();
        UserEntity entity = UserEntity.builder().id("1").email("test@test.com").build();

        when(mapper.map(user, UserEntity.class)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(Mono.just(entity));
        when(mapper.map(entity, User.class)).thenReturn(user);

        Mono<User> result = repositoryAdapter.save(user);

        StepVerifier.create(result)
                .expectNext(user)
                .verifyComplete();
    }
}
