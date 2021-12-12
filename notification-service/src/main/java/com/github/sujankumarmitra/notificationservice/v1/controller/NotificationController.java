package com.github.sujankumarmitra.notificationservice.v1.controller;

import com.github.sujankumarmitra.notificationservice.v1.controller.dto.CreateNotificationRequest;
import com.github.sujankumarmitra.notificationservice.v1.controller.dto.GetNotificationsResponse;
import com.github.sujankumarmitra.notificationservice.v1.controller.dto.NotificationDto;
import com.github.sujankumarmitra.notificationservice.v1.dao.NotificationDao;
import com.github.sujankumarmitra.notificationservice.v1.exception.ErrorDetails;
import com.github.sujankumarmitra.notificationservice.v1.exception.NotificationNotFoundException;
import com.github.sujankumarmitra.notificationservice.v1.model.Notification;
import com.github.sujankumarmitra.notificationservice.v1.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

import static com.github.sujankumarmitra.notificationservice.v1.config.OpenApiConfiguration.*;
import static java.net.URI.create;
import static org.springframework.http.ResponseEntity.*;

/**
 * @author skmitra
 * @since Sep 29/09/21, 2021
 */
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/notifications")
@Tag(
        name = "NotificationHttpController",
        description = "Controller for creating and acknowledging notifications"
)
@ApiSecurityScheme
@ApiSecurityResponse
public class NotificationController {

    public static final int DEFAULT_PAGE_SIZE = 10;
    @NonNull
    private final NotificationDao notificationDao;
    @NonNull
    private final NotificationService notificationService;

    @PostMapping
    @Operation(
            summary = "Create a notification for a consumer to consume",
            description = "Scopes required: NOTIFICATION_PRODUCE"
    )
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
    @ApiBadRequestResponse
    @PreAuthorize("hasAuthority('NOTIFICATION_PRODUCE')")
    public Mono<ResponseEntity<Void>> createNotification(@RequestBody @Valid CreateNotificationRequest request) {
        return notificationService.createNotification(request)
                .map(notificationId -> created(create(notificationId)).build());
    }


    @GetMapping("/{notificationId}")
    @Operation(
            summary = "Fetch a created notification",
            description = "Scopes required: NOTIFICATION_CONSUME"
    )
    @ApiResponse(responseCode = "200", description = "Server acknowledged the request")
    @ApiResponse(
            responseCode = "404",
            description = "Notification not found with given id",
            content = @Content(schema = @Schema)
    )
    @PreAuthorize("hasAuthority('NOTIFICATION_CONSUME')")
    public Mono<ResponseEntity<NotificationDto>> getNotification(Authentication authentication, @PathVariable String notificationId) {
        return notificationDao
                .findOne(notificationId, authentication.getName())
                .map(NotificationDto::new)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(notFound().build());
    }

    @GetMapping
    @PreAuthorize("hasAuthority('NOTIFICATION_CONSUME')")
    @Operation(
            summary = "Fetch notifications for a consumer",
            description = "Scopes required: NOTIFICATION_CONSUME"
    )
    @ApiResponse(responseCode = "200", description = "Server acknowledged the request")
    public Mono<ResponseEntity<GetNotificationsResponse>> getNotifications(Authentication authentication, @RequestParam(required = false) String lastInsertedId) {
        Flux<Notification> flux;
        String consumerId = authentication.getName();

        if (lastInsertedId == null) flux = notificationDao.find(consumerId, DEFAULT_PAGE_SIZE);
        else flux = notificationDao.find(consumerId, lastInsertedId, DEFAULT_PAGE_SIZE);

        return flux
                .collectList()
                .map(GetNotificationsResponse::new)
                .map(ResponseEntity::ok);
    }

    @Operation(
            summary = "Acknowledge a notification",
            description = "Scopes required: NOTIFICATION_CONSUME"
    )
    @ApiResponse(responseCode = "200", description = "Server acknowledged the request")
    @ApiResponse(responseCode = "404", description = "Notification not found with given id")
    @PatchMapping("/{notificationId}/ack")
    @PreAuthorize("hasAuthority('NOTIFICATION_CONSUME')")
    public Mono<ResponseEntity<Void>> acknowledgeNotification(Authentication authentication, @PathVariable String notificationId) {
        return notificationDao
                .setAcknowledged(notificationId, authentication.getName())
                .thenReturn(ok().build());
    }


    @ExceptionHandler(NotificationNotFoundException.class)
    public Mono<ResponseEntity<ErrorDetails>> notificationNotFoundExceptionHandler(NotificationNotFoundException ex) {
        return Mono.just(notFound().build());
    }
}