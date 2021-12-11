package com.github.sujankumarmitra.ebookprocessor.v1.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.sujankumarmitra.ebookprocessor.v1.config.RedisProperties;
import com.github.sujankumarmitra.ebookprocessor.v1.model.DefaultEBookProcessingStatus;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.UUID;

import static com.github.sujankumarmitra.ebookprocessor.v1.model.ProcessingState.PENDING;
import static java.lang.Boolean.TRUE;
import static java.nio.ByteBuffer.wrap;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author skmitra
 * @since Dec 11/12/21, 2021
 */
@DataRedisTest
@Slf4j
@Testcontainers
class RedisEBookProcessingStatusServiceTest {

    @Container
    private static final GenericContainer<?> REDIS_CONTAINER;
    @Autowired
    private ReactiveStringRedisTemplate redisTemplate;
    @Mock
    private RedisProperties redisProperties;
    private RedisEBookProcessingStatusService statusService;
    public static final int REDIS_PORT = 6379;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        statusService = new RedisEBookProcessingStatusService(
                redisTemplate,
                objectMapper,
                redisProperties);
    }

    static {
        REDIS_CONTAINER = new GenericContainer<>("redis")
                .withExposedPorts(REDIS_PORT);
    }

    @DynamicPropertySource
    static void registerProps(DynamicPropertyRegistry registry) {
        registry.add("spring.redis.host", REDIS_CONTAINER::getHost);
        registry.add("spring.redis.port", () -> REDIS_CONTAINER.getMappedPort(REDIS_PORT));
    }

    @Test
    void testAutowired() {
        assertThat(redisTemplate).isNotNull();
    }


    @Test
    void givenValidEBookProcessingStatus_whenSave_shouldSave() {
        DefaultEBookProcessingStatus processingStatus = new DefaultEBookProcessingStatus();

        processingStatus.setProcessId("process_id");
        processingStatus.setState(PENDING);
        processingStatus.setMessage("pending");

        Mockito.doReturn(Duration.ofDays(1).toMillis())
                .when(redisProperties).getDefaultKeyExpirationInMilliseconds();

        statusService
                .saveStatus(processingStatus)
                .as(StepVerifier::create)
                .expectSubscription()
                .expectComplete()
                .verify();

        redisTemplate.execute(connection -> connection.stringCommands()
                        .get(wrap("process_id".getBytes())))
                .as(StepVerifier::create)
                .expectSubscription()
                .consumeNextWith(buf -> {
                    StringBuilder body = new StringBuilder();
                    while (buf.hasRemaining()) {
                        body.append((char) buf.get());
                    }

                    log.info("Saved value {}", body);
                })
                .expectComplete()
                .verify();

    }

    @Test
    void givenValidProcessId_whenGetStatus_shouldGetStatus() throws JsonProcessingException {
        DefaultEBookProcessingStatus processingStatus = new DefaultEBookProcessingStatus();

        processingStatus.setProcessId("process_id");
        processingStatus.setState(PENDING);
        processingStatus.setMessage("message");

        byte[] value = objectMapper.writeValueAsBytes(processingStatus);
        byte[] key = "process_id".getBytes();

        redisTemplate.execute(connection -> connection
                        .stringCommands()
                        .set(wrap(key), wrap(value))
                        .filter(Boolean::booleanValue))
                .next()
                .as(StepVerifier::create)
                .expectSubscription()
                .expectNext(TRUE)
                .verifyComplete();


        statusService
                .getStatus("process_id")
                .as(StepVerifier::create)
                .expectSubscription()
                .expectNextMatches(fetchedStatus -> fetchedStatus.equals(processingStatus))
                .verifyComplete();
    }

    @Test
    void givenInvalidProcessId_whenGetStatus_shouldEmitEmpty() {
        statusService
                .getStatus(UUID.randomUUID().toString())
                .as(StepVerifier::create)
                .expectSubscription()
                .verifyComplete();
    }
}