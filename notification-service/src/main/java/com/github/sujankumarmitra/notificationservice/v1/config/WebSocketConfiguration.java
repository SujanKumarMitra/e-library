package com.github.sujankumarmitra.notificationservice.v1.config;

import com.github.sujankumarmitra.notificationservice.v1.service.socket.NotificationWebSocketHandler;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.tags.Tag;
import lombok.AllArgsConstructor;
import org.springdoc.core.customizers.OpenApiCustomiser;
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
    public HandlerMapping webSocketHandlerMapping(NotificationWebSocketHandler handler) {
        SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
        mapping.setOrder(Ordered.HIGHEST_PRECEDENCE);
        mapping.setUrlMap(Map.of("/api/v1/socket", handler));
        return mapping;
    }

    @Bean
    public OpenApiCustomiser webSocketPathCustomizer() {
        return openApi -> openApi.addTagsItem(new Tag()
                        .name("NotificationWebSocketController")
                        .description("Controller for receiving Notifications over WebSocket protocol")
                ).getPaths()
                .addPathItem("/api/v1/socket",
                        new PathItem()
                                .get(new Operation()
                                        .addParametersItem(new Parameter()
                                                .in("header")
                                                .name("Connection")
                                                .example("Upgrade")
                                                .schema(new Schema<>()
                                                        .type("string"))
                                                .required(true)
                                        ).addParametersItem(new Parameter()
                                                .in("header")
                                                .name("Upgrade")
                                                .example("websocket")
                                                .schema(new Schema<>()
                                                        .type("string"))
                                                .required(true))
                                        .addParametersItem(new Parameter()
                                                .in("header")
                                                .name("Sec-WebSocket-Key")
                                                .example("key")
                                                .schema(new Schema<>()
                                                        .type("string"))
                                                .required(true))
                                        .operationId("upgradeWebSocket")
                                        .summary("Upgrade HTTP to WebSocket")
                                        .description("Scopes required: NOTIFICATION_CONSUME")
                                        .addTagsItem("NotificationWebSocketController")
                                        .responses(new ApiResponses()
                                                .addApiResponse("101", new ApiResponse()
                                                        .description("Switching Protocols"))
                                        )

                                ));
    }


}

