package co.com.bancolombia.onboarding.consumer;

import co.com.bancolombia.onboarding.model.user.User;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;
import java.io.IOException;

class RestConsumerTest {

    private static RestConsumer restConsumer;
    private static MockWebServer mockBackEnd;

    @BeforeAll
    static void setUp() throws IOException {
        mockBackEnd = new MockWebServer();
        mockBackEnd.start();
        var webClient = WebClient.builder().baseUrl(mockBackEnd.url("/").toString()).build();
        restConsumer = new RestConsumer(webClient);
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockBackEnd.shutdown();
    }

    @Test
    @DisplayName("Validate external user fetch returning user details mapped to domain model")
    void validateFetchUser() {
        mockBackEnd.enqueue(new MockResponse()
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setResponseCode(HttpStatus.OK.value())
                .setBody("{\n" +
                        "  \"data\": {\n" +
                        "    \"id\": \"1\",\n" +
                        "    \"email\": \"george.bluth@reqres.in\",\n" +
                        "    \"first_name\": \"George\",\n" +
                        "    \"last_name\": \"Bluth\",\n" +
                        "    \"avatar\": \"https://reqres.in/img/faces/1-image.jpg\"\n" +
                        "  }\n" +
                        "}"));

        var response = restConsumer.fetchUser("1");

        StepVerifier.create(response)
                .expectNextMatches(user -> user.getId().equals("1") &&
                        user.getEmail().equals("george.bluth@reqres.in") &&
                        user.getFirstName().equals("George") &&
                        user.getLastName().equals("Bluth") &&
                        user.getAvatar().equals("https://reqres.in/img/faces/1-image.jpg"))
                .verifyComplete();
    }

    @Test
    @DisplayName("Validate external user fetch fallback on error")
    void validateFetchUserFallbackOnError() {
        mockBackEnd.enqueue(new MockResponse()
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value()));

        var response = restConsumer.fetchUser("1");

        StepVerifier.create(response)
                .expectNextMatches(user -> user.getId().equals("1") &&
                        user.getEmail().equals("george.bluth@reqres.in") &&
                        user.getFirstName().equals("George") &&
                        user.getLastName().equals("Bluth") &&
                        user.getAvatar().equals("https://reqres.in/img/faces/1-image.jpg"))
                .verifyComplete();
    }
}