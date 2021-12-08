package com.github.sujankumarmitra.libraryservice.v1.service.impl;

import com.github.sujankumarmitra.libraryservice.v1.model.Notification;
import com.github.sujankumarmitra.libraryservice.v1.service.NotificationService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * @author skmitra
 * @since Dec 07/12/21, 2021
 */
@Service
public class RemoteNotificationService implements NotificationService {
    @Override
    public Mono<Void> sendNotification(Notification notification) {
//        TODO implement me
        return Mono.error(new RuntimeException("implement me"));
    }
}
