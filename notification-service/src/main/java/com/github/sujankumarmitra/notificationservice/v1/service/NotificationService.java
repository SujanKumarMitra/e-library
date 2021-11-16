package com.github.sujankumarmitra.notificationservice.v1.service;

import com.github.sujankumarmitra.notificationservice.v1.model.Notification;
import reactor.core.publisher.Mono;

/**
 * @author skmitra
 * @since Nov 16/11/21, 2021
 */
public interface NotificationService {

    Mono<String> createNotification(Notification request);

}
