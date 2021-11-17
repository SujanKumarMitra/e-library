package com.github.sujankumarmitra.notificationservice.v1.controller;

import com.github.sujankumarmitra.notificationservice.v1.config.OpenApiConfiguration;
import com.github.sujankumarmitra.notificationservice.v1.model.Notification;
import com.github.sujankumarmitra.notificationservice.v1.security.AuthenticationToken;
import com.github.sujankumarmitra.notificationservice.v1.service.scheduler.Cancellable;
import com.github.sujankumarmitra.notificationservice.v1.service.scheduler.JobScheduler;
import com.github.sujankumarmitra.notificationservice.v1.service.sse.FluxSinkNotificationSseEmitter;
import com.github.sujankumarmitra.notificationservice.v1.service.sse.NotificationSseEmitter;
import com.github.sujankumarmitra.notificationservice.v1.service.sse.NotificationSseEmitterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Consumer;

import static java.lang.System.currentTimeMillis;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.springframework.http.MediaType.TEXT_EVENT_STREAM_VALUE;

/**
 * @author skmitra
 * @since Nov 12/11/21, 2021
 */
@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping("/api/v1/sse")
@Tag(
        name = "NotificationSseController",
        description = "### Controller for subscribing to Server-Sent Events of Notifications"
)
@OpenApiConfiguration.ApiSecurityScheme
@OpenApiConfiguration.ApiSecurityResponse
public class NotificationSseController {

    @NonNull
    private final NotificationSseEmitterService sseEmitterService;
    @NonNull
    private final JobScheduler jobScheduler;

    private static final Consumer<? super Object> NO_OP = __ -> {
    };


    @GetMapping(produces = TEXT_EVENT_STREAM_VALUE)
    @Operation(description = "# Subscribe to notification events")
    @ApiResponse(
            responseCode = "200",
            description = "Server acknowledged the request",
            content = @Content(mediaType = TEXT_EVENT_STREAM_VALUE)
    )
    @PreAuthorize("hasAuthority('NOTIFICATION_CONSUME')")
    public Flux<ServerSentEvent<Notification>> createNotificationSse(AuthenticationToken authentication) {
        return Flux.create(sink -> {

            long delay = authentication.getExpiresAt() - currentTimeMillis();
            Cancellable cancellable = jobScheduler.scheduleJob(sink::complete, delay, MILLISECONDS);

            NotificationSseEmitter emitter = new FluxSinkNotificationSseEmitter(
                    Mono.just(authentication.getName()), sink);

            sseEmitterService.addEmitter(emitter)
                    .subscribe(NO_OP,
                            th -> log.warn("Failed to add NotificationSseEmitter", th),
                            () -> log.info("Added SseEmitter for " + authentication.getName()));

            sink.onDispose(() -> {
                cancellable.cancel();
                sseEmitterService.removeEmitter(emitter)
                        .subscribe(NO_OP,
                                th -> log.warn("Failed to remove NotificationSseEmitter", th),
                                () -> log.info("Removed SseEmitter for " + authentication.getName()));
            });
        });
    }

}
