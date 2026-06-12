package co.com.bancolombia.onboarding.consumer.config;

import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import static org.assertj.core.api.Assertions.assertThat;

class RestConsumerConfigTest {

    @Test
    void testGetWebClient() {
        RestConsumerConfig config = new RestConsumerConfig("https://example.com", 5000);
        WebClient webClient = config.getWebClient();
        assertThat(webClient).isNotNull();
    }
}
