package com.github.sujankumarmitra.notificationservice.v1.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.sujankumarmitra.notificationservice.v1.service.events.NotificationEventService;
import com.github.sujankumarmitra.notificationservice.v1.service.socket.WebSocketSessionService;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import static reactor.core.publisher.Mono.just;

/**
 * @author skmitra
 * @since Oct 22/10/21, 2021
 */
@Component
@AllArgsConstructor
@Slf4j
public class NotificationWebSocketEventPublisher implements InitializingBean {

    @NonNull
    private final NotificationEventService eventService;
    @NonNull
    private final WebSocketSessionService sessionService;

    @Override
    public void afterPropertiesSet() {
        eventService.consumeEvents()
                .subscribe(event -> {
                    final String notificationJson = "{\"newNotificationId\": \"" + event.getNotificationId() + "\"}";

                    sessionService.getSessions(event.getConsumerId())
                            .subscribe(session -> session.send(
                                            just(session.textMessage(notificationJson)))
                                    .subscribe(v -> {
                                    }, err -> log.error("Error ", err)));
                }, err -> log.error("Error ", err));

    }
}
