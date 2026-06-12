package co.com.bancolombia.onboarding.usecase;

import co.com.bancolombia.onboarding.model.user.User;
import co.com.bancolombia.onboarding.model.user.gateways.UserNoSqlGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ProcessUserEventUseCaseTest {

    @Mock
    private UserNoSqlGateway userNoSqlGateway;

    private ProcessUserEventUseCase useCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        useCase = new ProcessUserEventUseCase(userNoSqlGateway);
    }

    @Test
    void shouldProcessUserEventAndSaveInUppercase() {
        // Arrange
        User inputUser = User.builder()
                .id("2")
                .firstName("Janet")
                .lastName("Weaver")
                .email("janet.weaver@reqres.in")
                .avatar("https://reqres.in/img/faces/2-image.jpg")
                .build();

        User expectedSavedUser = User.builder()
                .id("2")
                .firstName("JANET")
                .lastName("WEAVER")
                .email("JANET.WEAVER@REQRES.IN")
                .avatar("HTTPS://REQRES.IN/IMG/FACES/2-IMAGE.JPG")
                .build();

        when(userNoSqlGateway.save(any(User.class))).thenReturn(Mono.just(expectedSavedUser));

        // Act
        Mono<User> result = useCase.processUserEvent(inputUser);

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(savedUser -> {
                    assertThat(savedUser.getFirstName()).isEqualTo("JANET");
                    assertThat(savedUser.getLastName()).isEqualTo("WEAVER");
                    assertThat(savedUser.getEmail()).isEqualTo("JANET.WEAVER@REQRES.IN");
                    assertThat(savedUser.getAvatar()).isEqualTo("HTTPS://REQRES.IN/IMG/FACES/2-IMAGE.JPG");
                    return true;
                })
                .verifyComplete();

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userNoSqlGateway, times(1)).save(captor.capture());
        User capturedUser = captor.getValue();
        assertThat(capturedUser.getFirstName()).isEqualTo("JANET");
        assertThat(capturedUser.getLastName()).isEqualTo("WEAVER");
        assertThat(capturedUser.getEmail()).isEqualTo("JANET.WEAVER@REQRES.IN");
        assertThat(capturedUser.getAvatar()).isEqualTo("HTTPS://REQRES.IN/IMG/FACES/2-IMAGE.JPG");
    }

    @Test
    void shouldHandleNullFieldsGracefully() {
        // Arrange
        User inputUser = User.builder()
                .id("3")
                .build();

        User expectedSavedUser = User.builder()
                .id("3")
                .build();

        when(userNoSqlGateway.save(any(User.class))).thenReturn(Mono.just(expectedSavedUser));

        // Act
        Mono<User> result = useCase.processUserEvent(inputUser);

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(savedUser -> {
                    assertThat(savedUser.getFirstName()).isNull();
                    assertThat(savedUser.getLastName()).isNull();
                    return true;
                })
                .verifyComplete();
    }
}
