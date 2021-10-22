package com.github.sujankumarmitra.notificationservice.v1.config;

import com.github.sujankumarmitra.notificationservice.v1.service.socket.NotificationWebSocketHandler;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;

import java.util.Map;

/**
 * @author skmitra
 * @since Oct 05/10/21, 2021
 */
@Configuration
@AllArgsConstructor
public class WebSocketConfiguration {

    @Bean
    public HandlerMapping mapping(NotificationWebSocketHandler handler) {
        SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
        mapping.setOrder(Ordered.HIGHEST_PRECEDENCE);
        mapping.setUrlMap(Map.of("/api/v1/socket", handler));
        return mapping;
    }
}

