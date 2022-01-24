package com.github.sujankumarmitra.libraryservice.v1.service.impl;

import com.github.sujankumarmitra.libraryservice.v1.config.DefaultRemoteService;
import com.github.sujankumarmitra.libraryservice.v1.config.RemoteService;
import com.github.sujankumarmitra.libraryservice.v1.config.RemoteServiceRegistry;
import com.github.sujankumarmitra.libraryservice.v1.model.impl.DefaultNotification;
import com.github.sujankumarmitra.libraryservice.v1.security.AuthenticationToken;
import com.github.sujankumarmitra.libraryservice.v1.security.AuthenticationTokenExchangeFilterFunction;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static java.lang.System.currentTimeMillis;
import static java.util.Collections.emptyList;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.security.core.context.ReactiveSecurityContextHolder.withAuthentication;

/**
 * @author skmitra
 * @since Jan 24/01/22, 2022
 */
@WireMockTest
@ExtendWith(MockitoExtension.class)
class RemoteServiceNotificationServiceTest {
    public static final String TOKEN_VALUE = "token_value";
    private RemoteServiceNotificationService notificationService;
    @Mock
    private RemoteServiceRegistry registry;


    @BeforeEach
    void setUp(WireMockRuntimeInfo info) {
        Mockito.doReturn(notificationService(info))
                .when(registry).getService("notification-service");

        notificationService = new RemoteServiceNotificationService(
                WebClient.builder(),
                registry,
                new AuthenticationTokenExchangeFilterFunction()
        );
    }


    @Test
    void shouldSendHttpRequestWithAuthorizationHeader(WireMockRuntimeInfo runtimeInfo) {
        // given
        stubFor(post("/api/v1/notifications")
                .withHeader("Authorization", equalTo("Bearer " + TOKEN_VALUE))
                .willReturn(WireMock.aResponse()
                        .withStatus(CREATED.value())
                        .withHeader("Location", "notificationId")));

        DefaultNotification notification = new DefaultNotification();
        notification.setConsumerId("cid");
        notification.setCreatedAt(currentTimeMillis());
        notification.setPayload("payload");

        //when
        Mono<Void> completion = notificationService
                .sendNotification(notification)
                .contextWrite(withAuthentication(new AuthenticationToken(
                        TOKEN_VALUE,
                        "sub",
                        emptyList(),
                        currentTimeMillis())));


        // then

        StepVerifier.create(completion)
                .expectSubscription()
                .expectComplete()
                .verify();

    }

    private RemoteService notificationService(WireMockRuntimeInfo runtimeInfo) {
        DefaultRemoteService service = new DefaultRemoteService();

        service.setId("notification-service");
        service.setBaseUrl(runtimeInfo.getHttpBaseUrl());

        return service;
    }
}