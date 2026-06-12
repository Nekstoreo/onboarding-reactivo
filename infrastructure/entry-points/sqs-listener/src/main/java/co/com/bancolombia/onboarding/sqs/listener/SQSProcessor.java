package co.com.bancolombia.onboarding.sqs.listener;

import co.com.bancolombia.onboarding.model.user.User;
import co.com.bancolombia.onboarding.usecase.ProcessUserEventUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.model.Message;

import java.util.function.Function;

@Service
@Log4j2
@RequiredArgsConstructor
public class SQSProcessor implements Function<Message, Mono<Void>> {
    private final ProcessUserEventUseCase processUserEventUseCase;
    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> apply(Message message) {
        return Mono.fromCallable(() -> objectMapper.readValue(message.body(), User.class))
                .flatMap(processUserEventUseCase::processUserEvent)
                .doOnError(err -> log.error("Error processing SQS event: {}", err.getMessage(), err))
                .then();
    }
}
