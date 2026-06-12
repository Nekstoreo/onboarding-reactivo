package co.com.bancolombia.onboarding.sqs.sender;

import co.com.bancolombia.onboarding.model.user.User;
import co.com.bancolombia.onboarding.model.user.gateways.UserEventGateway;
import co.com.bancolombia.onboarding.sqs.sender.config.SQSSenderProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

@Service
@Log4j2
@RequiredArgsConstructor
public class SQSSender implements UserEventGateway {
    private final SQSSenderProperties properties;
    private final SqsAsyncClient client;
    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> publishUserCreated(User user) {
        return Mono.fromCallable(() -> objectMapper.writeValueAsString(user))
                .map(this::buildRequest)
                .flatMap(request -> Mono.fromFuture(client.sendMessage(request)))
                .doOnNext(response -> log.debug("Message sent {}", response.messageId()))
                .then();
    }

    private SendMessageRequest buildRequest(String message) {
        return SendMessageRequest.builder()
                .queueUrl(properties.queueUrl())
                .messageBody(message)
                .build();
    }
}
