package com.github.sujankumarmitra.notificationservice.v1.dao;

import com.github.sujankumarmitra.notificationservice.v1.model.Notification;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author skmitra
 * @since Sep 29/09/21, 2021
 */
public interface NotificationDao {

    Mono<String> insert(Notification notification);

    Flux<Notification> find(String consumerId, int count);

    Flux<Notification> find(String consumerId, String lastNotificationId, int count);

    Mono<Void> setAcknowledged(String notificationId);

}
