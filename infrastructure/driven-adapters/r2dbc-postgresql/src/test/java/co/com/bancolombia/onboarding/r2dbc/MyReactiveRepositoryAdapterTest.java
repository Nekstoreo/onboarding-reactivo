package co.com.bancolombia.onboarding.r2dbc;

import co.com.bancolombia.onboarding.model.user.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;
import reactor.core.publisher.Flux;
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

    @Test
    void mustFindAllValues() {
        User user1 = User.builder().id("1").email("test1@test.com").build();
        UserEntity entity1 = UserEntity.builder().id("1").email("test1@test.com").build();
        User user2 = User.builder().id("2").email("test2@test.com").build();
        UserEntity entity2 = UserEntity.builder().id("2").email("test2@test.com").build();

        when(repository.findAll()).thenReturn(Flux.just(entity1, entity2));
        when(mapper.map(entity1, User.class)).thenReturn(user1);
        when(mapper.map(entity2, User.class)).thenReturn(user2);

        Flux<User> result = repositoryAdapter.findAll();

        StepVerifier.create(result)
                .expectNext(user1)
                .expectNext(user2)
                .verifyComplete();
    }

    @Test
    void mustFindValuesByName() {
        User user = User.builder().id("1").firstName("George").email("test@test.com").build();
        UserEntity entity = UserEntity.builder().id("1").firstName("George").email("test@test.com").build();

        when(repository.findAllByFirstNameIgnoreCase("George")).thenReturn(Flux.just(entity));
        when(mapper.map(entity, User.class)).thenReturn(user);

        Flux<User> result = repositoryAdapter.findByName("George");

        StepVerifier.create(result)
                .expectNext(user)
                .verifyComplete();
    }
}
