package com.github.sujankumarmitra.notificationservice.v1.service.sse;

import lombok.Getter;
import reactor.core.publisher.Mono;

/**
 * @author skmitra
 * @since Nov 13/11/21, 2021
 */
@Getter
public abstract class AbstractNotificationSseEmitter implements NotificationSseEmitter {

    private final Mono<String> consumerId;

    public AbstractNotificationSseEmitter(Mono<String> consumerId) {
        this.consumerId = consumerId;
    }

}
