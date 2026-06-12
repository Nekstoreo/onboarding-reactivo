package co.com.bancolombia.onboarding.sqs.sender;

import co.com.bancolombia.onboarding.model.user.User;
import co.com.bancolombia.onboarding.sqs.sender.config.SQSSenderProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;
import reactor.test.StepVerifier;

import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SQSSenderTest {

    @Mock
    private SqsAsyncClient client;

    @Mock
    private SQSSenderProperties properties;

    private ObjectMapper objectMapper;
    private SQSSender sqsSender;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        sqsSender = new SQSSender(properties, client, objectMapper);
    }

    @Test
    void shouldPublishUserCreatedEventSuccessfully() {
        // Arrange
        User user = User.builder()
                .id("1")
                .email("george.bluth@reqres.in")
                .firstName("George")
                .lastName("Bluth")
                .avatar("https://reqres.in/img/faces/1-image.jpg")
                .build();

        SendMessageResponse response = SendMessageResponse.builder()
                .messageId("msg-12345")
                .build();

        when(properties.queueUrl()).thenReturn("http://localhost:4566/000000000000/user-created-events");
        when(client.sendMessage(any(SendMessageRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(response));

        // Act
        var result = sqsSender.publishUserCreated(user);

        // Assert
        StepVerifier.create(result)
                .verifyComplete();

        verify(client, times(1)).sendMessage(any(SendMessageRequest.class));
    }
}
