package com.github.sujankumarmitra.ebookprocessor.v1.service.impl;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

/**
 * @author skmitra
 * @since Dec 12/12/21, 2021
 */
class ReactorContextTest {
    @Test
    void testContextFromSynchronousSink() {
        Mono.just(1)
                .handle((val, sink) -> {
                    String value = sink.currentContext().get("key");
                    sink.next(val + value);
                })
                .contextWrite(context -> context.put("key", "one"))
                .as(StepVerifier::create)
                .expectSubscription()
                .expectNext("1one")
                .verifyComplete();
    }
}
