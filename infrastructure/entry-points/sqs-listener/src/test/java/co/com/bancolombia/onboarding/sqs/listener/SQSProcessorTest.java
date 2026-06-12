package co.com.bancolombia.onboarding.sqs.listener;

import co.com.bancolombia.onboarding.model.user.User;
import co.com.bancolombia.onboarding.usecase.ProcessUserEventUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import software.amazon.awssdk.services.sqs.model.Message;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SQSProcessorTest {

    @Mock
    private ProcessUserEventUseCase processUserEventUseCase;

    private ObjectMapper objectMapper;

    private SQSProcessor processor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        objectMapper = new ObjectMapper();
        processor = new SQSProcessor(processUserEventUseCase, objectMapper);
    }

    @Test
    void shouldProcessMessageSuccessfully() throws Exception {
        // Arrange
        User user = User.builder()
                .id("2")
                .firstName("Janet")
                .lastName("Weaver")
                .email("janet.weaver@reqres.in")
                .build();

        String body = objectMapper.writeValueAsString(user);
        Message message = Message.builder().body(body).build();

        when(processUserEventUseCase.processUserEvent(any(User.class))).thenReturn(Mono.just(user));

        // Act
        Mono<Void> result = processor.apply(message);

        // Assert
        StepVerifier.create(result)
                .verifyComplete();

        verify(processUserEventUseCase, times(1)).processUserEvent(any(User.class));
    }

    @Test
    void shouldHandleDeserializationError() {
        // Arrange
        Message message = Message.builder().body("invalid json").build();

        // Act
        Mono<Void> result = processor.apply(message);

        // Assert
        StepVerifier.create(result)
                .expectError()
                .verify();

        verify(processUserEventUseCase, never()).processUserEvent(any(User.class));
    }
}
