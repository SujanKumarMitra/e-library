package com.github.sujankumarmitra.ebookprocessor.v1.security;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.test.StepVerifier;

import java.net.URI;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.reactive.function.client.ClientRequest.create;

/**
 * @author skmitra
 * @since Dec 11/12/21, 2021
 */
@Slf4j
@ExtendWith(MockitoExtension.class)
class AuthenticationTokenExchangeFilterFunctionTest {

    @Mock
    private ExchangeFunction exchangeFunction;
    AuthenticationTokenExchangeFilterFunction filterFunction;

    @BeforeEach
    void setUp() {
        filterFunction = new AuthenticationTokenExchangeFilterFunction();
    }

    @Test
    void givenRequest_whenFilter_shouldSetHeader() {

        Mockito.doAnswer(invocation -> {
                    ClientRequest request = invocation.getArgument(0);
                    String first = request.headers().getFirst(AUTHORIZATION);

                    log.info("Auth Header '{}'", first);

                    if(first == null || !first.startsWith("Bearer")) {
                        return Mono.error(new RuntimeException("token missing"));
                    }
                    return Mono.just(ClientResponse.create(OK).build());
                })
                .when(exchangeFunction)
                .exchange(any());

        ClientRequest request = create(POST, URI.create("/api/v1/any-path")).build();

        AuthenticationToken token = new AuthenticationToken("token", "", List.of(), System.currentTimeMillis());

        filterFunction.filter(request, exchangeFunction)
                .contextWrite(ReactiveSecurityContextHolder.withAuthentication(token))
                .as(StepVerifier::create)
                .expectSubscription()
                .expectNextMatches( response -> response.statusCode() == OK)
                .verifyComplete();
    }
}