package com.github.sujankumarmitra.notificationservice.v1.dao;

import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import reactor.core.publisher.Mono;

import java.util.function.Consumer;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static reactor.core.publisher.Flux.using;
import static reactor.core.publisher.Mono.fromDirect;
import static reactor.core.scheduler.Schedulers.immediate;

/**
 * @author skmitra
 * @since Oct 04/10/21, 2021
 */
class MonoResourceCleanupTest {

    @Test
    void givenBuffer_doCleanupAfterConsumed() {
        DataBufferFactory bufferFactory = new DefaultDataBufferFactory();
        DataBuffer dataBuffer = bufferFactory.wrap("hello".getBytes(UTF_8));

        @SuppressWarnings("unchecked")
        Consumer<DataBuffer> cleanupConsumer = mock(Consumer.class);
        @SuppressWarnings("unchecked")
        Consumer<DataBuffer> subscriberConsumer = mock(Consumer.class);

        doNothing().when(cleanupConsumer).accept(any());
        doNothing().when(subscriberConsumer).accept(any());
        InOrder inOrder = inOrder(subscriberConsumer, cleanupConsumer);

        Mono<DataBuffer> bufferMono = fromDirect(
                using(() -> dataBuffer,
                        buf -> Mono.fromCallable(() -> buf),
                        cleanupConsumer))
                .publishOn(immediate())
                .subscribeOn(immediate());


        bufferMono.subscribe(subscriberConsumer);

        inOrder.verify(subscriberConsumer).accept(any());
        inOrder.verify(cleanupConsumer).accept(any());
    }


}
