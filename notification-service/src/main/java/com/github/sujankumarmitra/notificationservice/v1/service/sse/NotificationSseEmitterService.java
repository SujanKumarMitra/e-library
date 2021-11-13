package com.github.sujankumarmitra.notificationservice.v1.service.sse;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author skmitra
 * @since Nov 13/11/21, 2021
 */
public interface NotificationSseEmitterService {

    Mono<Void> addEmitter(NotificationSseEmitter emitter);

    Mono<Void> removeEmitter(NotificationSseEmitter emitter);

    Flux<NotificationSseEmitter> getEmitters(String consumerId);
}
