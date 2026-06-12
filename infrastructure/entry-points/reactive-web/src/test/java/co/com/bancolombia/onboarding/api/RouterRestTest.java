package co.com.bancolombia.onboarding.api;

import co.com.bancolombia.onboarding.model.user.User;
import co.com.bancolombia.onboarding.model.user.UserNotFoundException;
import co.com.bancolombia.onboarding.usecase.CreateUserUseCase;
import co.com.bancolombia.onboarding.usecase.GetAllUsersUseCase;
import co.com.bancolombia.onboarding.usecase.GetUserByIdUseCase;
import co.com.bancolombia.onboarding.usecase.GetUsersByNameUseCase;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {RouterRest.class, Handler.class, co.com.bancolombia.onboarding.api.config.GlobalExceptionHandler.class})
@WebFluxTest
class RouterRestTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private CreateUserUseCase createUserUseCase;

    @MockitoBean
    private GetUserByIdUseCase getUserByIdUseCase;

    @MockitoBean
    private GetAllUsersUseCase getAllUsersUseCase;

    @MockitoBean
    private GetUsersByNameUseCase getUsersByNameUseCase;

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

    @Test
    void testGetUserByIdSuccess() {
        User user = User.builder()
                .id("1")
                .email("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .avatar("avatar_url")
                .build();

        when(getUserByIdUseCase.getUserById("1")).thenReturn(Mono.just(user));

        webTestClient.get()
                .uri("/api/users/1")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(User.class)
                .value(responseUser -> {
                    Assertions.assertThat(responseUser.getId()).isEqualTo("1");
                    Assertions.assertThat(responseUser.getFirstName()).isEqualTo("John");
                });
    }

    @Test
    void testGetUserByIdNotFound() {
        when(getUserByIdUseCase.getUserById("999")).thenReturn(Mono.error(new UserNotFoundException("User with id 999 not found")));

        webTestClient.get()
                .uri("/api/users/999")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.message").isEqualTo("User with id 999 not found");
    }

    @Test
    void testGetAllUsers() {
        User user = User.builder()
                .id("1")
                .firstName("John")
                .build();

        when(getAllUsersUseCase.getAllUsers()).thenReturn(Flux.just(user));

        webTestClient.get()
                .uri("/api/users")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(User.class)
                .hasSize(1)
                .value(list -> {
                    Assertions.assertThat(list.get(0).getFirstName()).isEqualTo("John");
                });
    }

    @Test
    void testGetUsersByName() {
        User user = User.builder()
                .id("1")
                .firstName("John")
                .build();

        when(getUsersByNameUseCase.getUsersByName("John")).thenReturn(Flux.just(user));

        webTestClient.get()
                .uri("/api/users/search?name=John")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(User.class)
                .hasSize(1)
                .value(list -> {
                    Assertions.assertThat(list.get(0).getFirstName()).isEqualTo("John");
                });
    }
}
