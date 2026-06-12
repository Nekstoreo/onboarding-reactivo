package co.com.bancolombia.onboarding.api;

import co.com.bancolombia.onboarding.model.user.User;
import co.com.bancolombia.onboarding.usecase.CreateUserUseCase;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {RouterRest.class, Handler.class})
@WebFluxTest
class RouterRestTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private CreateUserUseCase createUserUseCase;

    @Test
    void testListenGETUseCase() {
        webTestClient.get()
                .uri("/api/usecase/path")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(userResponse -> {
                            Assertions.assertThat(userResponse).isEmpty();
                        }
                );
    }

    @Test
    void testListenGETOtherUseCase() {
        webTestClient.get()
                .uri("/api/otherusercase/path")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(userResponse -> {
                            Assertions.assertThat(userResponse).isEmpty();
                        }
                );
    }

    @Test
    void testListenPOSTUseCase() {
        webTestClient.post()
                .uri("/api/usecase/otherpath")
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue("")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(userResponse -> {
                            Assertions.assertThat(userResponse).isEmpty();
                        }
                );
    }

    @Test
    void testCreateUser() {
        User user = User.builder()
                .id("1")
                .email("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .avatar("avatar_url")
                .build();

        when(createUserUseCase.createUser("1")).thenReturn(Mono.just(user));

        webTestClient.post()
                .uri("/api/users/1")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(User.class)
                .value(responseUser -> {
                    Assertions.assertThat(responseUser.getId()).isEqualTo("1");
                    Assertions.assertThat(responseUser.getEmail()).isEqualTo("test@example.com");
                    Assertions.assertThat(responseUser.getFirstName()).isEqualTo("John");
                    Assertions.assertThat(responseUser.getLastName()).isEqualTo("Doe");
                    Assertions.assertThat(responseUser.getAvatar()).isEqualTo("avatar_url");
                });
    }
}
