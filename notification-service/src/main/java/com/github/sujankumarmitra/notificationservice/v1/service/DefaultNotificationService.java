package com.github.sujankumarmitra.notificationservice.v1.service;

import com.github.sujankumarmitra.notificationservice.v1.dao.NotificationDao;
import com.github.sujankumarmitra.notificationservice.v1.model.DefaultNotification;
import com.github.sujankumarmitra.notificationservice.v1.model.Notification;
import com.github.sujankumarmitra.notificationservice.v1.service.events.NotificationEventService;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * @author skmitra
 * @since Nov 16/11/21, 2021
 */
@Slf4j
@Service
@AllArgsConstructor
public class DefaultNotificationService implements NotificationService {

    @NonNull
    private final NotificationDao notificationDao;
    @NonNull
    private final NotificationEventService eventService;


    @Override
    public Mono<String> createNotification(Notification request) {
        return notificationDao
                .insert(request)
                .map(id -> buildNotificationWithId(request, id))
                .flatMap(this::publishNotificationEvent)
                .map(Notification::getId);
    }

    private Mono<Notification> publishNotificationEvent(Notification notification) {
        return eventService
                .publishEvent(notification)
                .thenReturn(notification)
                .onErrorResume(th -> {
                    log.warn("Failed to publish notification event {}", notification, th);
                    return Mono.just(notification);
                });
    }

    private Notification buildNotificationWithId(Notification request, String id) {
        return DefaultNotification
                .newBuilder()
                .id(id)
                .consumerId(request.getConsumerId())
                .createdAt(request.getCreatedAt())
                .payload(request.getPayload())
                .acknowledged(request.isAcknowledged())
                .build();
    }
}
