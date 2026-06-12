package co.com.bancolombia.onboarding.usecase;

import co.com.bancolombia.onboarding.model.user.User;
import co.com.bancolombia.onboarding.model.user.gateways.UserGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetUsersByNameUseCaseTest {

    @Mock
    private UserGateway userGateway;

    private GetUsersByNameUseCase getUsersByNameUseCase;

    private User sampleUser;

    @BeforeEach
    void setUp() {
        getUsersByNameUseCase = new GetUsersByNameUseCase(userGateway);
        sampleUser = User.builder()
                .id("1")
                .email("george.bluth@reqres.in")
                .firstName("George")
                .lastName("Bluth")
                .avatar("https://reqres.in/img/faces/1-image.jpg")
                .build();
    }

    @Test
    void shouldReturnUsersMatchingNameFromDatabase() {
        // Arrange
        when(userGateway.findByName("George")).thenReturn(Flux.just(sampleUser));

        // Act
        Flux<User> result = getUsersByNameUseCase.getUsersByName("George");

        // Assert
        StepVerifier.create(result)
                .expectNext(sampleUser)
                .verifyComplete();

        verify(userGateway, times(1)).findByName("George");
        verifyNoMoreInteractions(userGateway);
    }
}
