package co.com.bancolombia.onboarding;

import co.com.bancolombia.onboarding.dynamodb.UserNoSqlEntity;
import co.com.bancolombia.onboarding.model.user.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("local")
class SQSIntegrationTest {

    @Autowired
    private SqsAsyncClient sqsAsyncClient;

    @Autowired
    private DynamoDbEnhancedAsyncClient dynamoDbEnhancedAsyncClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldProcessMessageFromSQSAndSaveToDynamoDBInUppercase() throws Exception {
        // Arrange
        User testUser = User.builder()
                .id("integration-test-1")
                .firstName("steve")
                .lastName("rogers")
                .email("steve.rogers@shield.gov")
                .avatar("https://reqres.in/img/faces/3-image.jpg")
                .build();

        String body = objectMapper.writeValueAsString(testUser);

        SendMessageRequest sendMessageRequest = SendMessageRequest.builder()
                .queueUrl("http://localhost:4566/000000000000/user-created-events")
                .messageBody(body)
                .build();

        // Act - Send message to local SQS queue
        sqsAsyncClient.sendMessage(sendMessageRequest).get();

        // Assert - Wait and poll local DynamoDB table in a non-blocking reactive way
        var table = dynamoDbEnhancedAsyncClient.table("users-uppercase", TableSchema.fromBean(UserNoSqlEntity.class));

        UserNoSqlEntity savedEntity = Mono.defer(() -> Mono.fromFuture(table.getItem(Key.builder().partitionValue("integration-test-1").build())))
                .repeatWhenEmpty(20, repeat -> repeat.delayElements(Duration.ofMillis(500)))
                .block(Duration.ofSeconds(10));

        // Verify entity was processed, transformed to uppercase, and saved in DynamoDB
        assertNotNull(savedEntity, "User was not processed and saved in DynamoDB within timeout");
        assertEquals("STEVE", savedEntity.getFirstName());
        assertEquals("ROGERS", savedEntity.getLastName());
        assertEquals("STEVE.ROGERS@SHIELD.GOV", savedEntity.getEmail());
        assertEquals("HTTPS://REQRES.IN/IMG/FACES/3-IMAGE.JPG", savedEntity.getAvatar());
    }
}
