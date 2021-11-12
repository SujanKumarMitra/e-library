package com.github.sujankumarmitra.notificationservice.v1.controller;

import com.github.sujankumarmitra.notificationservice.v1.model.Notification;
import com.github.sujankumarmitra.notificationservice.v1.service.events.NotificationEventService;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;

import static org.springframework.http.MediaType.TEXT_EVENT_STREAM_VALUE;

/**
 * @author skmitra
 * @since Nov 12/11/21, 2021
 */
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/sse")
public class NotificationSseController {

    @NonNull
    private final NotificationEventService notificationEventService;


    @GetMapping(produces = TEXT_EVENT_STREAM_VALUE)
    @PreAuthorize("hasAuthority('NOTIFICATION_CONSUME')")
    public Flux<ServerSentEvent<Notification>> createNotificationSse(Authentication authentication) {
        return Flux.create(sink -> {
            Disposable disposable = notificationEventService
                    .consumeEvents()
                    .filter(notification -> notification.getConsumerId().equals(authentication.getName()))
                    .map(notification -> ServerSentEvent
                            .<Notification>builder()
                            .id(notification.getId())
                            .data(notification)
                            .build())
                    .subscribe(sink::next, sink::error, sink::complete);
            sink.onDispose(disposable::dispose);
        });
    }

}
