package co.com.bancolombia.onboarding.api.config;

import co.com.bancolombia.onboarding.api.Handler;
import co.com.bancolombia.onboarding.api.RouterRest;
import co.com.bancolombia.onboarding.usecase.CreateUserUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

import co.com.bancolombia.onboarding.usecase.GetAllUsersUseCase;
import co.com.bancolombia.onboarding.usecase.GetUserByIdUseCase;
import co.com.bancolombia.onboarding.usecase.GetUsersByNameUseCase;

import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {RouterRest.class, Handler.class})
@WebFluxTest
@Import({CorsConfig.class, SecurityHeadersConfig.class})
class ConfigTest {

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
    void corsConfigurationShouldAllowOrigins() {
        when(getAllUsersUseCase.getAllUsers()).thenReturn(Flux.empty());

        webTestClient.get()
                .uri("/api/users")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals("Content-Security-Policy",
                        "default-src 'self'; frame-ancestors 'self'; form-action 'self'")
                .expectHeader().valueEquals("Strict-Transport-Security", "max-age=31536000; includeSubDomains; preload")
                .expectHeader().valueEquals("X-Content-Type-Options", "nosniff")
                .expectHeader().doesNotExist("Server")
                .expectHeader().valueEquals("Cache-Control", "no-store")
                .expectHeader().valueEquals("Pragma", "no-cache")
                .expectHeader().valueEquals("Referrer-Policy", "strict-origin-when-cross-origin");
    }

}