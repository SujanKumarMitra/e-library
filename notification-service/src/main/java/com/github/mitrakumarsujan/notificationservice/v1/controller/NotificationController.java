package com.github.mitrakumarsujan.notificationservice.v1.controller;

import com.github.mitrakumarsujan.notificationservice.v1.controller.dto.CreateNotificationRequest;
import com.github.mitrakumarsujan.notificationservice.v1.controller.dto.GetNotificationsResponse;
import com.github.mitrakumarsujan.notificationservice.v1.dao.NotificationDao;
import com.github.mitrakumarsujan.notificationservice.v1.exception.ErrorDetails;
import com.github.mitrakumarsujan.notificationservice.v1.exception.NotificationNotFoundException;
import com.github.mitrakumarsujan.notificationservice.v1.model.Notification;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;

import static org.springframework.http.ResponseEntity.accepted;
import static org.springframework.http.ResponseEntity.created;

/**
 * @author skmitra
 * @since Sep 29/09/21, 2021
 */
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    public static final int DEFAULT_PAGE_SIZE = 10;
    @NonNull
    private final NotificationDao notificationDao;

    @PostMapping
    public Mono<ResponseEntity<Void>> createNotification(@RequestBody CreateNotificationRequest request) {
        return notificationDao
                .insert(request)
                .map(URI::create)
                .map(uri -> created(uri).build());
    }

    @GetMapping("/{consumerId}")
    public Mono<ResponseEntity<GetNotificationsResponse>> getNotifications(@PathVariable String consumerId, @RequestParam(required = false) String lastInsertedId) {
        Flux<Notification> flux;
        if (lastInsertedId == null) flux = notificationDao.find(consumerId, DEFAULT_PAGE_SIZE);
        else flux = notificationDao.find(consumerId, lastInsertedId, DEFAULT_PAGE_SIZE);

        return flux
                .collectList()
                .map(GetNotificationsResponse::new)
                .map(ResponseEntity::ok);
    }

    @PatchMapping("/{notificationId}/ack")
    public Mono<ResponseEntity<Void>> acknowledgeNotification(@PathVariable String notificationId) {
        return notificationDao
                .setAcknowledged(notificationId)
                .map(__ -> accepted().build());
    }


    @ExceptionHandler(NotificationNotFoundException.class)
    public Mono<ResponseEntity<ErrorDetails>> notificationNotFoundExceptionHandler(NotificationNotFoundException ex) {
        return Mono.just(ResponseEntity.notFound().build());
    }
}