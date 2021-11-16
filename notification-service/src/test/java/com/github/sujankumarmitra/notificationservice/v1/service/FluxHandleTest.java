package com.github.sujankumarmitra.notificationservice.v1.service;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

/**
 * @author skmitra
 * @since Nov 16/11/21, 2021
 */
class FluxHandleTest {


    @Test
    void whenHandleDoesNotInvokeOnNext_nextSignalShouldNotBePropagated() {
        Flux.range(1, 10)
                .handle((i, sink) -> System.out.println("Handling " + i))
                .as(StepVerifier::create)
                .expectSubscription()
                .expectNextCount(0)
                .verifyComplete();
    }
}
