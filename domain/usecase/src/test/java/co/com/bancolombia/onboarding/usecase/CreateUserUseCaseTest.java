package co.com.bancolombia.onboarding.usecase;

import co.com.bancolombia.onboarding.model.user.User;
import co.com.bancolombia.onboarding.model.user.gateways.ExternalUserGateway;
import co.com.bancolombia.onboarding.model.user.gateways.UserEventGateway;
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
class CreateUserUseCaseTest {

    @Mock
    private UserGateway userGateway;

    @Mock
    private ExternalUserGateway externalUserGateway;

    @Mock
    private UserEventGateway userEventGateway;

    private CreateUserUseCase createUserUseCase;

    private User sampleUser;

    @BeforeEach
    void setUp() {
        createUserUseCase = new CreateUserUseCase(userGateway, externalUserGateway, userEventGateway);
        sampleUser = User.builder()
                .id("1")
                .email("george.bluth@reqres.in")
                .firstName("George")
                .lastName("Bluth")
                .avatar("https://reqres.in/img/faces/1-image.jpg")
                .build();
    }

    @Test
    void shouldReturnExistingUserWhenUserAlreadyExistsLocally() {
        // Arrange
        when(userGateway.findById("1")).thenReturn(Mono.just(sampleUser));

        // Act
        Mono<User> result = createUserUseCase.createUser("1");

        // Assert
        StepVerifier.create(result)
                .expectNext(sampleUser)
                .verifyComplete();

        verify(userGateway, times(1)).findById("1");
        verifyNoMoreInteractions(userGateway);
        verifyNoInteractions(externalUserGateway, userEventGateway);
    }

    @Test
    void shouldFetchSaveAndPublishWhenUserDoesNotExistLocally() {
        // Arrange
        when(userGateway.findById("1")).thenReturn(Mono.empty());
        when(externalUserGateway.fetchUser("1")).thenReturn(Mono.just(sampleUser));
        when(userGateway.save(any(User.class))).thenReturn(Mono.just(sampleUser));
        when(userEventGateway.publishUserCreated(any(User.class))).thenReturn(Mono.empty());

        // Act
        Mono<User> result = createUserUseCase.createUser("1");

        // Assert
        StepVerifier.create(result)
                .expectNext(sampleUser)
                .verifyComplete();

        verify(userGateway, times(1)).findById("1");
        verify(externalUserGateway, times(1)).fetchUser("1");
        verify(userGateway, times(1)).save(sampleUser);
        verify(userEventGateway, times(1)).publishUserCreated(sampleUser);
    }
}
