package com.github.sujankumarmitra.notificationservice.v1.service;

import com.github.sujankumarmitra.notificationservice.v1.model.Notification;
import com.github.sujankumarmitra.notificationservice.v1.service.events.NotificationEventService;
import com.github.sujankumarmitra.notificationservice.v1.service.sse.NotificationSseEmitter;
import com.github.sujankumarmitra.notificationservice.v1.service.sse.NotificationSseEmitterService;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Component;
import reactor.util.function.Tuples;

/**
 * @author skmitra
 * @since Nov 13/11/21, 2021
 */
@Component
@AllArgsConstructor
public class SseNotificationEventPublisher implements InitializingBean {

    @NonNull
    private final NotificationEventService notificationEventService;
    @NonNull
    private final NotificationSseEmitterService emitterService;


    @Override
    public void afterPropertiesSet() {
        notificationEventService
                .consumeEvents()
                .flatMap(notification ->
                        emitterService
                                .getEmitters(notification.getConsumerId())
                                .map(emitter -> Tuples.of(notification, emitter))
                ).subscribe(tuple -> sendEvent(tuple.getT1(), tuple.getT2()));
    }

    private void sendEvent(Notification notification, NotificationSseEmitter emitter) {
        ServerSentEvent<Notification> serverSentEvent = ServerSentEvent
                .<Notification>builder()
                .id(notification.getId())
                .event("new_notification")
                .data(notification)
                .build();
        emitter.emitSse(serverSentEvent);
    }
}
