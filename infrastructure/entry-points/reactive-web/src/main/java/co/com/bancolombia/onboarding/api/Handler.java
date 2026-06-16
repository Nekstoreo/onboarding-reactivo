package co.com.bancolombia.onboarding.api;

import co.com.bancolombia.onboarding.model.user.User;
import co.com.bancolombia.onboarding.usecase.CreateUserUseCase;
import co.com.bancolombia.onboarding.usecase.GetAllUsersUseCase;
import co.com.bancolombia.onboarding.usecase.GetUserByIdUseCase;
import co.com.bancolombia.onboarding.usecase.GetUsersByNameUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@Log4j2
@RequiredArgsConstructor
public class Handler {
    private final CreateUserUseCase createUserUseCase;
    private final GetUserByIdUseCase getUserByIdUseCase;
    private final GetAllUsersUseCase getAllUsersUseCase;
    private final GetUsersByNameUseCase getUsersByNameUseCase;

    public Mono<ServerResponse> createUser(ServerRequest serverRequest) {
        String id = serverRequest.pathVariable("id");
        log.info("Request received to create user with ID: {}", id);
        return createUserUseCase.createUser(id)
                .doOnSuccess(user -> log.info("User created successfully with ID: {}", user.getId()))
                .flatMap(user -> ServerResponse.status(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(user));
    }

    public Mono<ServerResponse> getUserById(ServerRequest serverRequest) {
        String id = serverRequest.pathVariable("id");
        log.info("Request received to get user by ID: {}", id);
        return getUserByIdUseCase.getUserById(id)
                .doOnSuccess(user -> log.info("User retrieved successfully with ID: {}", user.getId()))
                .flatMap(user -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(user));
    }

    public Mono<ServerResponse> getAllUsers(ServerRequest serverRequest) {
        log.info("Request received to list all users");
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(getAllUsersUseCase.getAllUsers(), User.class);
    }

    public Mono<ServerResponse> getUsersByName(ServerRequest serverRequest) {
        String name = serverRequest.queryParam("name")
                .orElse("");
        log.info("Request received to search users by name matching: {}", name);
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(getUsersByNameUseCase.getUsersByName(name), User.class);
    }
}
