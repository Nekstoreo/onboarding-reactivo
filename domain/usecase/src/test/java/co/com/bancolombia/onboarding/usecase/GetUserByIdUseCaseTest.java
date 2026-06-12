package co.com.bancolombia.onboarding.usecase;

import co.com.bancolombia.onboarding.model.user.User;
import co.com.bancolombia.onboarding.model.user.UserNotFoundException;
import co.com.bancolombia.onboarding.model.user.gateways.UserCacheGateway;
import co.com.bancolombia.onboarding.model.user.gateways.UserGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetUserByIdUseCaseTest {

    @Mock
    private UserGateway userGateway;

    @Mock
    private UserCacheGateway userCacheGateway;

    private GetUserByIdUseCase getUserByIdUseCase;

    private User sampleUser;

    @BeforeEach
    void setUp() {
        getUserByIdUseCase = new GetUserByIdUseCase(userGateway, userCacheGateway);
        sampleUser = User.builder()
                .id("1")
                .email("george.bluth@reqres.in")
                .firstName("George")
                .lastName("Bluth")
                .avatar("https://reqres.in/img/faces/1-image.jpg")
                .build();
    }

    @Test
    void shouldReturnUserFromCacheWhenExistsInCache() {
        // Arrange
        when(userCacheGateway.findById("1")).thenReturn(Mono.just(sampleUser));

        // Act
        Mono<User> result = getUserByIdUseCase.getUserById("1");

        // Assert
        StepVerifier.create(result)
                .expectNext(sampleUser)
                .verifyComplete();

        verify(userCacheGateway, times(1)).findById("1");
        verifyNoMoreInteractions(userCacheGateway);
        verifyNoInteractions(userGateway);
    }

    @Test
    void shouldReturnUserFromDatabaseAndSaveToCacheWhenCacheMissAndExistsInDatabase() {
        // Arrange
        when(userCacheGateway.findById("1")).thenReturn(Mono.empty());
        when(userGateway.findById("1")).thenReturn(Mono.just(sampleUser));
        when(userCacheGateway.save(any(User.class))).thenReturn(Mono.just(sampleUser));

        // Act
        Mono<User> result = getUserByIdUseCase.getUserById("1");

        // Assert
        StepVerifier.create(result)
                .expectNext(sampleUser)
                .verifyComplete();

        verify(userCacheGateway, times(1)).findById("1");
        verify(userGateway, times(1)).findById("1");
        verify(userCacheGateway, times(1)).save(sampleUser);
    }

    @Test
    void shouldReturnErrorWhenUserDoesNotExistInCacheNorDatabase() {
        // Arrange
        when(userCacheGateway.findById("1")).thenReturn(Mono.empty());
        when(userGateway.findById("1")).thenReturn(Mono.empty());

        // Act
        Mono<User> result = getUserByIdUseCase.getUserById("1");

        // Assert
        StepVerifier.create(result)
                .expectError(UserNotFoundException.class)
                .verify();

        verify(userCacheGateway, times(1)).findById("1");
        verify(userGateway, times(1)).findById("1");
        verify(userCacheGateway, never()).save(any(User.class));
    }
}
