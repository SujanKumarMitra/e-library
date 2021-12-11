//package com.github.sujankumarmitra.ebookprocessor.v1.service.impl;
//
//import com.auth0.jwt.JWT;
//import com.github.sujankumarmitra.ebookprocessor.v1.config.DefaultRemoteService;
//import com.github.sujankumarmitra.ebookprocessor.v1.config.RemoteServiceRegistry;
//import com.github.sujankumarmitra.ebookprocessor.v1.model.EBookFormat;
//import com.github.sujankumarmitra.ebookprocessor.v1.security.AuthenticationToken;
//import com.github.sujankumarmitra.ebookprocessor.v1.security.AuthenticationTokenExchangeFilterFunction;
//import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
//import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
//import com.github.tomakehurst.wiremock.junit5.WireMockTest;
//import com.github.tomakehurst.wiremock.matching.EqualToPattern;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.web.reactive.function.client.WebClient;
//import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
//import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
//import reactor.test.StepVerifier;
//
//import java.util.List;
//
//import static com.auth0.jwt.algorithms.Algorithm.HMAC256;
//import static com.github.sujankumarmitra.ebookprocessor.v1.security.AuthenticationTokenExchangeFilterFunction.BEARER_PREFIX;
//import static com.github.sujankumarmitra.ebookprocessor.v1.service.impl.RemoteServiceEBookService.GetEBookResponse.BOOK_TYPE;
//import static com.github.tomakehurst.wiremock.client.WireMock.get;
//import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
//import static org.springframework.http.HttpHeaders.AUTHORIZATION;
//import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
//import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
//import static org.springframework.security.core.context.ReactiveSecurityContextHolder.withAuthentication;
//
///**
// * @author skmitra
// * @since Dec 11/12/21, 2021
// */
//@WireMockTest
//@ExtendWith(MockitoExtension.class)
//class RemoteServiceEBookServiceTest {
//
//    @Mock
//    private RemoteServiceRegistry registry;
//    private final ObjectMapper objectMapper = new ObjectMapper();
//    private RemoteServiceEBookService eBookService;
//
//    @BeforeEach
//    void setUp(WireMockRuntimeInfo runtimeInfo) {
//        DefaultRemoteService remoteService = new DefaultRemoteService();
//
//        remoteService.setId("library-service");
//        remoteService.setBaseUrl(runtimeInfo.getHttpBaseUrl());
//
//        Mockito.doReturn(remoteService)
//                .when(registry).getService("library-service");
//
//        eBookService = new RemoteServiceEBookService(
//                WebClient.builder(),
//                registry,
//                new AuthenticationTokenExchangeFilterFunction());
//    }
//
//    @Test
//    void givenStubbingForLibraryService_whenGetBook_shouldGetBook() throws JsonProcessingException {
//
//        GetEBookResponse response = new GetEBookResponse();
//        response.setFormat(EBookFormat.PDF);
//        response.setId("book_id");
//        response.setType(BOOK_TYPE);
//
//        String body = objectMapper.writeValueAsString(response);
//
//
//        String jwtToken = JWT.create().sign(HMAC256("secret"));
//        stubFor(get("/api/v1/books/book_id")
//                .withHeader(AUTHORIZATION, new EqualToPattern(BEARER_PREFIX + jwtToken))
//                .willReturn(new ResponseDefinitionBuilder()
//                        .withStatus(200)
//                        .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
//                        .withBody(body)));
//
//        AuthenticationToken token = new AuthenticationToken(jwtToken, "", List.of(), System.currentTimeMillis());
//
//        eBookService
//                .getEBook("book_id")
//                .contextWrite(withAuthentication(token))
//                .as(StepVerifier::create)
//                .expectSubscription()
//                .expectNextMatches(actual -> actual.equals(response))
//                .verifyComplete();
//    }
//}