package co.com.bancolombia.onboarding.redis.template;

import co.com.bancolombia.onboarding.model.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveValueOperations;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class ReactiveRedisTemplateAdapterOperationsTest {

    @Mock
    private ReactiveRedisConnectionFactory connectionFactory;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private ReactiveRedisTemplate<String, User> reactiveRedisTemplate;

    @Mock
    private ReactiveValueOperations<String, User> reactiveValueOperations;

    private ReactiveRedisTemplateAdapter adapter;

    private User sampleUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        sampleUser = User.builder()
                .id("1")
                .email("george.bluth@reqres.in")
                .firstName("George")
                .lastName("Bluth")
                .avatar("https://reqres.in/img/faces/1-image.jpg")
                .build();

        when(objectMapper.map(sampleUser, User.class)).thenReturn(sampleUser);

        adapter = new ReactiveRedisTemplateAdapter(connectionFactory, objectMapper);

        // Inject the mocked template using ReflectionTestUtils
        ReflectionTestUtils.setField(adapter, "template", reactiveRedisTemplate);

        when(reactiveRedisTemplate.opsForValue()).thenReturn(reactiveValueOperations);
    }

    @Test
    void testSave() {
        when(reactiveValueOperations.set("1", sampleUser)).thenReturn(Mono.just(true));

        StepVerifier.create(adapter.save(sampleUser))
                .expectNext(sampleUser)
                .verifyComplete();
    }

    @Test
    void testSaveWithExpiration() {
        when(reactiveValueOperations.set("1", sampleUser)).thenReturn(Mono.just(true));
        when(reactiveRedisTemplate.expire(eq("1"), any(Duration.class))).thenReturn(Mono.just(true));

        StepVerifier.create(adapter.save("1", sampleUser, 100))
                .expectNext(sampleUser)
                .verifyComplete();
    }

    @Test
    void testFindById() {
        when(reactiveValueOperations.get("1")).thenReturn(Mono.just(sampleUser));

        StepVerifier.create(adapter.findById("1"))
                .expectNext(sampleUser)
                .verifyComplete();
    }

    @Test
    void testFindByIdEmpty() {
        when(reactiveValueOperations.get("1")).thenReturn(Mono.empty());

        StepVerifier.create(adapter.findById("1"))
                .verifyComplete();
    }
}