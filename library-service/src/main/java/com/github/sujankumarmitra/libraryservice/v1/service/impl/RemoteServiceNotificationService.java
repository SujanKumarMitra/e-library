package com.github.sujankumarmitra.libraryservice.v1.service.impl;

import com.github.sujankumarmitra.libraryservice.v1.config.InternalUser;
import com.github.sujankumarmitra.libraryservice.v1.config.RemoteServiceRegistry;
import com.github.sujankumarmitra.libraryservice.v1.model.Notification;
import com.github.sujankumarmitra.libraryservice.v1.service.NotificationService;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.reactive.function.client.ExchangeFilterFunctions.basicAuthentication;

/**
 * @author skmitra
 * @since Jan 24/01/22, 2022
 */
@Service
public class RemoteServiceNotificationService implements NotificationService {

    public static final String NOTIFICATIONS_URI = "/api/internal/notifications";
    private final WebClient webClient;

    public RemoteServiceNotificationService(WebClient.Builder builder,
                                            InternalUser internalUser,
                                            RemoteServiceRegistry serviceRegistry) {
        this.webClient = builder
                .baseUrl(serviceRegistry.getService("notification-service").getBaseUrl())
                .filter(basicAuthentication(internalUser.getUsername(), internalUser.getPassword()))
                .defaultHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .build();
    }

    @Override
    public Mono<Void> sendNotification(Notification notification) {
        return webClient
                .post()
                .uri(NOTIFICATIONS_URI)
                .body(BodyInserters.fromValue(notification))
                .retrieve()
                .toBodilessEntity()
                .then();
    }
}
