package com.github.sujankumarmitra.notificationservice.v1.controller;

import com.github.sujankumarmitra.notificationservice.v1.config.OpenApiConfiguration;
import com.github.sujankumarmitra.notificationservice.v1.controller.dto.CreateNotificationRequest;
import com.github.sujankumarmitra.notificationservice.v1.controller.dto.GetNotificationsResponse;
import com.github.sujankumarmitra.notificationservice.v1.dao.NotificationDao;
import com.github.sujankumarmitra.notificationservice.v1.exception.ErrorDetails;
import com.github.sujankumarmitra.notificationservice.v1.exception.NotificationNotFoundException;
import com.github.sujankumarmitra.notificationservice.v1.model.Notification;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.net.URI;

import static org.springframework.http.ResponseEntity.*;

/**
 * @author skmitra
 * @since Sep 29/09/21, 2021
 */
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/notifications")
@Tag(
        name = "NotificationController",
        description = "### Controller for creating and acknowledging notifications"
)
public class NotificationController {

    public static final int DEFAULT_PAGE_SIZE = 10;
    @NonNull
    private final NotificationDao notificationDao;

    @PostMapping
    @Operation(description = "# Create a notification for a consumer to consume")
    @ApiResponse(
            responseCode = "201",
            headers = @Header(
                    name = "Location",
                    description = "Unique ID pointing to this notification",
                    schema = @Schema(
                            example = "7d553b6b-c6e4-42a7-bc8d-7cda07909b2f"
                    )
            )
    )
    @OpenApiConfiguration.ApiBadRequestResponse
    public Mono<ResponseEntity<Void>> createNotification(@RequestBody @Valid CreateNotificationRequest request) {
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

    @Operation(description = "# Acknowledge a notification")
    @ApiResponse(responseCode = "200", description = "Server acknowledged the request")
    @ApiResponse(responseCode = "404", description = "Notification not found with given id")
    @PatchMapping("/{notificationId}/ack")
    public Mono<ResponseEntity<Void>> acknowledgeNotification(@PathVariable String notificationId) {
        return notificationDao
                .setAcknowledged(notificationId)
                .map(__ -> ok().build());
    }


    @ExceptionHandler(NotificationNotFoundException.class)
    public Mono<ResponseEntity<ErrorDetails>> notificationNotFoundExceptionHandler(NotificationNotFoundException ex) {
        return Mono.just(notFound().build());
    }
}