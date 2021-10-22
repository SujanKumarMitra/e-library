package com.github.sujankumarmitra.notificationservice.v1.service.socket;

import lombok.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SynchronousSink;

import java.util.*;

/**
 * @author skmitra
 * @since Oct 05/10/21, 2021
 */
@Service
public class MapWebSocketSessionService implements WebSocketSessionService {

    private final Map<String, Set<WebSocketSession>> sessionMap;

    public MapWebSocketSessionService() {
        this(Collections.synchronizedMap(new HashMap<>()));
    }

    public MapWebSocketSessionService(Map<String, Set<WebSocketSession>> sessionMap) {
        this.sessionMap = sessionMap;
    }

    @Override
    public Mono<Void> addSession(@NonNull WebSocketSession session) {
        return session.getHandshakeInfo()
                .getPrincipal()
                .cast(Authentication.class)
                .map(Authentication::getName)
                .map(consumerId -> sessionMap.compute(consumerId, (cId, sessions) -> addSessionToMap(session, sessions)))
                .then();
    }

    private Set<WebSocketSession> addSessionToMap(WebSocketSession session, Set<WebSocketSession> sessions) {
        if (sessions == null)
            sessions = new HashSet<>();
        sessions.add(session);
        return sessions;
    }

    @Override
    public Mono<Void> removeSession(WebSocketSession session) {
        return session.getHandshakeInfo()
                .getPrincipal()
                .cast(Authentication.class)
                .map(Authentication::getName)
                .handle((consumerId, sink) -> handleGetSocketSessions(consumerId, session, sink));
    }

    private void handleGetSocketSessions(String consumerId, WebSocketSession session, SynchronousSink<Void> sink) {
        Set<WebSocketSession> webSocketSessions = sessionMap.getOrDefault(consumerId, null);
        if (webSocketSessions != null)
            webSocketSessions.remove(session);
        sink.complete();
    }

    @Override
    public Flux<WebSocketSession> getSessions(String consumerId) {
        Set<WebSocketSession> webSocketSessions = sessionMap.getOrDefault(consumerId, Collections.emptySet());
        return Flux.fromIterable(webSocketSessions);
    }
}
