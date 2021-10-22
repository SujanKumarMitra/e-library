package com.github.sujankumarmitra.notificationservice.v1.service.socket;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static reactor.core.publisher.Mono.deferContextual;
import static reactor.core.publisher.Mono.just;

/**
 * @author skmitra
 * @since Oct 05/10/21, 2021
 */
class MonoContextTest {

    @Test
    void testContextPropagationWithMonoZipWith() {
        Mono<String> helloWorld = just("hello")
                .zipWith(deferContextual(
                        ctx -> just(ctx.get("key")).cast(String.class)))
                .map(tup -> tup.getT1() + " " + tup.getT2())
                .contextWrite(ctx -> ctx.put("key", "world"));


        StepVerifier.create(helloWorld)
                .expectSubscription()
                .expectNext("hello world")
                .verifyComplete();
    }

    @Test
    void testContextPropagationWithMonoThen() {
        Mono<String> hello = Mono.empty()
                .then(deferContextual(
                        ctx -> just(ctx.get("key")).cast(String.class)))
                .contextWrite(ctx -> ctx.put("key", "hello"));


        StepVerifier.create(hello)
                .expectSubscription()
                .expectNext("hello")
                .verifyComplete();
    }
}
