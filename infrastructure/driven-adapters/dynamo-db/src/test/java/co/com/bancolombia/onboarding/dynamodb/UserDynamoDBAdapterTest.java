package co.com.bancolombia.onboarding.dynamodb;

import co.com.bancolombia.onboarding.model.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.reactivecommons.utils.ObjectMapper;
import reactor.test.StepVerifier;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class UserDynamoDBAdapterTest {

    @Mock
    private DynamoDbEnhancedAsyncClient dynamoDbEnhancedAsyncClient;

    @Mock
    private ObjectMapper mapper;

    @Mock
    private DynamoDbAsyncTable<UserNoSqlEntity> userTable;

    private UserDynamoDBAdapter adapter;
    private User user;
    private UserNoSqlEntity entity;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        when(dynamoDbEnhancedAsyncClient.table(eq("users-uppercase"), any(TableSchema.class)))
                .thenReturn(userTable);

        adapter = new UserDynamoDBAdapter(dynamoDbEnhancedAsyncClient, mapper);

        user = User.builder()
                .id("2")
                .firstName("JANET")
                .lastName("WEAVER")
                .email("JANET.WEAVER@REQRES.IN")
                .avatar("HTTPS://REQRES.IN/IMG/FACES/2-IMAGE.JPG")
                .build();

        entity = new UserNoSqlEntity("2", "JANET.WEAVER@REQRES.IN", "JANET", "WEAVER", "HTTPS://REQRES.IN/IMG/FACES/2-IMAGE.JPG");
    }

    @Test
    void shouldSaveUserToDynamoDB() {
        when(mapper.map(user, UserNoSqlEntity.class)).thenReturn(entity);
        when(userTable.putItem(entity)).thenReturn(CompletableFuture.runAsync(() -> {}));
        when(mapper.map(entity, User.class)).thenReturn(user);

        StepVerifier.create(adapter.save(user))
                .expectNext(user)
                .verifyComplete();
    }
}
