package com.github.sujankumarmitra.notificationservice.v1.service.events;

import com.github.sujankumarmitra.notificationservice.v1.model.Notification;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author skmitra
 * @since Oct 04/10/21, 2021
 */
public interface NotificationEventService {
    Mono<Void> publishEvent(Notification event);

    Flux<Notification> consumeEvents();
}
