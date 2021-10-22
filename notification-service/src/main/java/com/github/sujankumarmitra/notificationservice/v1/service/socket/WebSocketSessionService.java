package com.github.sujankumarmitra.notificationservice.v1.service.socket;

import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author skmitra
 * @since Oct 05/10/21, 2021
 */
public interface WebSocketSessionService {

    Mono<Void> addSession(WebSocketSession session);

    Mono<Void> removeSession(WebSocketSession session);

    Flux<WebSocketSession> getSessions(String consumerId);

}
