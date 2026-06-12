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
class GetAllUsersUseCaseTest {

    @Mock
    private UserGateway userGateway;

    private GetAllUsersUseCase getAllUsersUseCase;

    private User sampleUser1;
    private User sampleUser2;

    @BeforeEach
    void setUp() {
        getAllUsersUseCase = new GetAllUsersUseCase(userGateway);
        sampleUser1 = User.builder()
                .id("1")
                .email("george.bluth@reqres.in")
                .firstName("George")
                .lastName("Bluth")
                .avatar("https://reqres.in/img/faces/1-image.jpg")
                .build();
        sampleUser2 = User.builder()
                .id("2")
                .email("janet.weaver@reqres.in")
                .firstName("Janet")
                .lastName("Weaver")
                .avatar("https://reqres.in/img/faces/2-image.jpg")
                .build();
    }

    @Test
    void shouldReturnAllUsersFromDatabase() {
        // Arrange
        when(userGateway.findAll()).thenReturn(Flux.just(sampleUser1, sampleUser2));

        // Act
        Flux<User> result = getAllUsersUseCase.getAllUsers();

        // Assert
        StepVerifier.create(result)
                .expectNext(sampleUser1)
                .expectNext(sampleUser2)
                .verifyComplete();

        verify(userGateway, times(1)).findAll();
        verifyNoMoreInteractions(userGateway);
    }
}
