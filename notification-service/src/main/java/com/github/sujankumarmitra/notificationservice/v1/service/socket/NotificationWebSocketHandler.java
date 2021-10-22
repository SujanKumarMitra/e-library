package com.github.sujankumarmitra.notificationservice.v1.service.socket;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;

/**
 * @author skmitra
 * @since Oct 05/10/21, 2021
 */
@Component
@AllArgsConstructor
@Slf4j
public class NotificationWebSocketHandler implements WebSocketHandler {

    @NonNull
    private final WebSocketSessionService sessionService;

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        Mono<Void> addSessionMono = sessionService
                .addSession(session)
                .doOnSuccess(v -> log.info("Session Added {}", session));

        Mono<Void> removeSessionMono = session.closeStatus()
                .flatMap(status -> sessionService.removeSession(session))
                .doOnSuccess(v -> log.info("Session Removed {}", session));

        return Mono.when(addSessionMono, removeSessionMono);
    }


}
