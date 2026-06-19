package co.com.bancolombia.onboarding.api;

import co.com.bancolombia.onboarding.model.user.User;
import co.com.bancolombia.onboarding.model.user.ValidationException;
import co.com.bancolombia.onboarding.usecase.CreateUserUseCase;
import co.com.bancolombia.onboarding.usecase.GetAllUsersUseCase;
import co.com.bancolombia.onboarding.usecase.GetUserByIdUseCase;
import co.com.bancolombia.onboarding.usecase.GetUsersByNameUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class Handler {
    public record UserRequest(String id) {}

    private final CreateUserUseCase createUserUseCase;
    private final GetUserByIdUseCase getUserByIdUseCase;
    private final GetAllUsersUseCase getAllUsersUseCase;
    private final GetUsersByNameUseCase getUsersByNameUseCase;

    public Mono<ServerResponse> createUser(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(UserRequest.class)
                .switchIfEmpty(Mono.error(new ValidationException("Request body is required")))
                .flatMap(userRequest -> {
                    String id = userRequest.id();
                    if (id == null || id.isBlank() || !id.matches("^\\d+$")) {
                        return Mono.error(new ValidationException("User ID must be a positive integer"));
                    }
                    return createUserUseCase.createUser(id)
                            .flatMap(user -> ServerResponse.status(HttpStatus.CREATED)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .bodyValue(user));
                });
    }

    public Mono<ServerResponse> getUserById(ServerRequest serverRequest) {
        String id = serverRequest.pathVariable("id");
        if (id.isBlank() || !id.matches("^\\d+$")) {
            return Mono.error(new ValidationException("User ID must be a positive integer"));
        }
        return getUserByIdUseCase.getUserById(id)
                .flatMap(user -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(user));
    }

    public Mono<ServerResponse> getUsers(ServerRequest serverRequest) {
        String name = serverRequest.queryParam("name").orElse("");
        if (!name.isBlank()) {
            return ServerResponse.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(getUsersByNameUseCase.getUsersByName(name), User.class);
        }
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(getAllUsersUseCase.getAllUsers(), User.class);
    }
}
