package com.github.sujankumarmitra.notificationservice.v1.service.socket;

import com.github.sujankumarmitra.notificationservice.v1.security.VerifiedJwtAuthenticationToken;
import com.github.sujankumarmitra.notificationservice.v1.service.scheduler.Cancellable;
import com.github.sujankumarmitra.notificationservice.v1.service.scheduler.JobScheduler;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;

import static java.lang.System.currentTimeMillis;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.springframework.web.reactive.socket.CloseStatus.POLICY_VIOLATION;

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
    @NonNull
    private final JobScheduler jobScheduler;

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        Mono<Void> addSessionMono = sessionService
                .addSession(session)
                .doOnSuccess(v -> log.info("Session Added {}", session));


        Mono<Cancellable> cancellable = session
                .getHandshakeInfo()
                .getPrincipal()
                .cast(VerifiedJwtAuthenticationToken.class)
                .map(VerifiedJwtAuthenticationToken::getExpiresAt)
                .map(expiresAt -> expiresAt - currentTimeMillis())
//                .doOnNext(delay -> System.out.println("Duration: " + Duration.ofMillis(delay)))
                .map(delay -> jobScheduler
                        .scheduleJob(() -> session.close(POLICY_VIOLATION).subscribe(), delay, MILLISECONDS));

        Mono<Void> removeSessionMono = session.closeStatus()
                .flatMap(status -> sessionService.removeSession(session))
                .doOnSuccess(v -> log.info("Session Removed {}", session))
                .then(cancellable)
                .doOnSuccess(Cancellable::cancel)
                .then();

        return Mono.when(addSessionMono, removeSessionMono, cancellable);
    }


}
