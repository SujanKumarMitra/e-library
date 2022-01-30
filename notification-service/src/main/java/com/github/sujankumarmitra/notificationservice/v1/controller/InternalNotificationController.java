package com.github.sujankumarmitra.notificationservice.v1.controller;

import com.github.sujankumarmitra.notificationservice.v1.controller.dto.CreateNotificationRequest;
import com.github.sujankumarmitra.notificationservice.v1.dao.NotificationDao;
import com.github.sujankumarmitra.notificationservice.v1.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

import static java.net.URI.create;
import static org.springframework.http.ResponseEntity.created;

/**
 * @author skmitra
 * @since Sep 29/09/21, 2021
 */
@RestController
@AllArgsConstructor
@RequestMapping("/api/internal/notifications")
public class InternalNotificationController {

    public static final int DEFAULT_PAGE_SIZE = 10;
    @NonNull
    private final NotificationDao notificationDao;
    @NonNull
    private final NotificationService notificationService;

    @PostMapping
    @Operation(hidden = true)
    public Mono<ResponseEntity<Void>> createNotification(@RequestBody @Valid CreateNotificationRequest request) {
        return notificationService.createNotification(request)
                .map(notificationId -> created(create(notificationId)).build());
    }

}