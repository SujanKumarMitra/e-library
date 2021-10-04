package com.github.sujankumarmitra.notificationservice.v1.controller;

import com.github.sujankumarmitra.notificationservice.v1.model.JacksonNewNotificationEvent;
import com.github.sujankumarmitra.notificationservice.v1.service.events.NotificationEventService;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;

/**
 * @author skmitra
 * @since Oct 04/10/21, 2021
 */
@RestController
@AllArgsConstructor
public class KafkaController {

    @NonNull
    NotificationEventService eventService;

    @PostMapping
    public Mono<Void> post(@RequestBody JacksonNewNotificationEvent event) {
        return eventService.publishEvent(event);
    }


    @PostConstruct
    void init() {
        eventService
                .consumeEvents()
                .subscribe(System.out::println);
    }
}