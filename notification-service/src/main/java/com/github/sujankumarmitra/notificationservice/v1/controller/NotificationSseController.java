package com.github.sujankumarmitra.notificationservice.v1.controller;

import com.github.sujankumarmitra.notificationservice.v1.config.OpenApiConfiguration;
import com.github.sujankumarmitra.notificationservice.v1.model.Notification;
import com.github.sujankumarmitra.notificationservice.v1.service.events.NotificationEventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(
        name = "NotificationSseController",
        description = "### Controller for subscribing to Server-Sent Events of Notifications"
)
@OpenApiConfiguration.ApiSecurityScheme
@OpenApiConfiguration.ApiSecurityResponse
public class NotificationSseController {

    @NonNull
    private final NotificationEventService notificationEventService;


    @GetMapping(produces = TEXT_EVENT_STREAM_VALUE)
    @Operation(description = "# Subscribe to notification events")
    @ApiResponse(
            responseCode = "200",
            description = "Server acknowledged the request",
            content = @Content(mediaType = TEXT_EVENT_STREAM_VALUE)
    )
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
            sink.onDispose(disposable);
        });
    }

}
