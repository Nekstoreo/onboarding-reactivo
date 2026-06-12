package co.com.bancolombia.onboarding.dynamodb;

import co.com.bancolombia.onboarding.dynamodb.helper.TemplateAdapterOperations;
import co.com.bancolombia.onboarding.model.user.User;
import co.com.bancolombia.onboarding.model.user.gateways.UserNoSqlGateway;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;

@Repository
public class UserDynamoDBAdapter extends TemplateAdapterOperations<User, String, UserNoSqlEntity>
        implements UserNoSqlGateway {

    public UserDynamoDBAdapter(DynamoDbEnhancedAsyncClient connectionFactory, ObjectMapper mapper) {
        super(connectionFactory, mapper, d -> mapper.map(d, User.class), "users-uppercase");
    }
}
