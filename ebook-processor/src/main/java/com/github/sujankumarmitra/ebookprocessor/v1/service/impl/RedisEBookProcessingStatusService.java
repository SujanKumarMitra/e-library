package com.github.sujankumarmitra.ebookprocessor.v1.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.sujankumarmitra.ebookprocessor.v1.config.RedisProperties;
import com.github.sujankumarmitra.ebookprocessor.v1.exception.RedisStoreException;
import com.github.sujankumarmitra.ebookprocessor.v1.model.impl.DefaultEBookProcessingStatus;
import com.github.sujankumarmitra.ebookprocessor.v1.model.EBookProcessingStatus;
import com.github.sujankumarmitra.ebookprocessor.v1.service.EBookProcessingStatusService;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SynchronousSink;

import java.io.IOException;
import java.nio.ByteBuffer;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * @author skmitra
 * @since Dec 11/12/21, 2021
 */
@Service
@Slf4j
@AllArgsConstructor
public class RedisEBookProcessingStatusService implements EBookProcessingStatusService {
    @NonNull
    private final ReactiveStringRedisTemplate redisTemplate;
    @NonNull
    private final ObjectMapper objectMapper;
    @NonNull
    private final RedisProperties redisProperties;

    @Override
    public Mono<EBookProcessingStatus> getStatus(@NonNull String processId) {
        return redisTemplate.createMono(connection -> Mono.create(sink -> {
            ByteBuffer keyBuf = ByteBuffer.wrap(processId.getBytes());

            connection.stringCommands()
                    .get(keyBuf)
                    .map(this::mapToByteArray)
                    .handle(this::deserializeToPojo)
                    .subscribe(sink::success,
                            err -> {
                                log.warn("Error while retrieving EBookProcessingStatus", err);
                                sink.error(err);
                            },
                            sink::success);
        }));
    }

    private void deserializeToPojo(byte[] bytes, SynchronousSink<EBookProcessingStatus> sink) {
        try {
            EBookProcessingStatus statusObject = objectMapper
                    .readValue(bytes, DefaultEBookProcessingStatus.class);
            sink.next(statusObject);
        } catch (IOException ex) {
            log.warn("Error in deserializing Redis returned value", ex);
            sink.error(ex);
        }
    }

    private byte[] mapToByteArray(ByteBuffer valueBuf) {

        if (valueBuf.hasArray()) {
            log.debug("ByteBuffer.array() is accessible, returning ByteBuffer.array()");
            return valueBuf.array();
        }

        log.debug("ByteBuffer.array() is inaccessible, creating, reading and returning a byte[]");

        int size = valueBuf.remaining();
        byte[] bytes = new byte[size];

        int idx = 0;
        while (valueBuf.hasRemaining()) {
            bytes[idx++] = valueBuf.get();
        }
        return bytes;
    }

    @Override
    public Mono<Void> saveStatus(EBookProcessingStatus processingStatus) {
        return redisTemplate.createMono(connection -> Mono.create(sink -> {
            String processId = processingStatus.getProcessId();

            byte[] key = processId.getBytes();
            byte[] value;

            try {
                value = objectMapper.writeValueAsBytes(processingStatus);
            } catch (JsonProcessingException ex) {
                log.warn("Error in serializing EBookProcessingStatus to json", ex);
                sink.error(ex);
                return;
            }

            ByteBuffer keyBuffer = ByteBuffer.wrap(key);
            ByteBuffer valueBuffer = ByteBuffer.wrap(value);
            Long expirationInMilliseconds = redisProperties.getDefaultKeyExpirationInMilliseconds();

            connection
                    .stringCommands()
                    .setEX(keyBuffer, valueBuffer, Expiration.from(expirationInMilliseconds, MILLISECONDS))
                    .filter(Boolean::booleanValue)
                    .switchIfEmpty(Mono.error(() -> new RedisStoreException("redis SETEX returned false")))
                    .subscribe(s -> sink.success(),
                            err -> {
                                log.warn("Error when saving EBookProcessingStatus", err);
                                sink.error(err);
                            },
                            sink::success);
        }));

    }
}
