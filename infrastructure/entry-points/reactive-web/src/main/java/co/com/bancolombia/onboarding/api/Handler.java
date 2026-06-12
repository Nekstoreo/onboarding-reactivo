package co.com.bancolombia.onboarding.api;

import co.com.bancolombia.onboarding.model.user.User;
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
    private final CreateUserUseCase createUserUseCase;
    private final GetUserByIdUseCase getUserByIdUseCase;
    private final GetAllUsersUseCase getAllUsersUseCase;
    private final GetUsersByNameUseCase getUsersByNameUseCase;

    public Mono<ServerResponse> listenGETUseCase(ServerRequest serverRequest) {
        // useCase.logic();
        return ServerResponse.ok().bodyValue("");
    }

    public Mono<ServerResponse> listenGETOtherUseCase(ServerRequest serverRequest) {
        // useCase2.logic();
        return ServerResponse.ok().bodyValue("");
    }

    public Mono<ServerResponse> listenPOSTUseCase(ServerRequest serverRequest) {
        // useCase.logic();
        return ServerResponse.ok().bodyValue("");
    }

    public Mono<ServerResponse> createUser(ServerRequest serverRequest) {
        String id = serverRequest.pathVariable("id");
        return createUserUseCase.createUser(id)
                .flatMap(user -> ServerResponse.status(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(user));
    }

    public Mono<ServerResponse> getUserById(ServerRequest serverRequest) {
        String id = serverRequest.pathVariable("id");
        return getUserByIdUseCase.getUserById(id)
                .flatMap(user -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(user));
    }

    public Mono<ServerResponse> getAllUsers(ServerRequest serverRequest) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(getAllUsersUseCase.getAllUsers(), User.class);
    }

    public Mono<ServerResponse> getUsersByName(ServerRequest serverRequest) {
        String name = serverRequest.queryParam("name")
                .orElse("");
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(getUsersByNameUseCase.getUsersByName(name), User.class);
    }
}
