package co.com.bancolombia.onboarding.sqs.sender.config;

import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;

import static org.assertj.core.api.Assertions.assertThat;

class SQSSenderConfigTest {

    @Test
    void testConfigSqsWithEndpoint() {
        SQSSenderConfig config = new SQSSenderConfig();
        SQSSenderProperties properties = new SQSSenderProperties("us-east-1", "http://queue", "http://localhost:4566");

        SqsAsyncClient client = config.configSqs(properties);

        assertThat(client).isNotNull();
        assertThat(config.jacksonObjectMapper()).isNotNull();
    }

    @Test
    void testConfigSqsWithoutEndpoint() {
        SQSSenderConfig config = new SQSSenderConfig();
        SQSSenderProperties properties = new SQSSenderProperties("us-east-1", "http://queue", null);

        SqsAsyncClient client = config.configSqs(properties);

        assertThat(client).isNotNull();
    }
}
