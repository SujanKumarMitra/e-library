package com.github.sujankumarmitra.ebookprocessor.v1.service.impl;

import com.github.sujankumarmitra.ebookprocessor.v1.config.RemoteServiceRegistry;
import com.github.sujankumarmitra.ebookprocessor.v1.exception.EBookNotFoundException;
import com.github.sujankumarmitra.ebookprocessor.v1.model.EBookSegment;
import com.github.sujankumarmitra.ebookprocessor.v1.security.AuthenticationTokenExchangeFilterFunction;
import com.github.sujankumarmitra.ebookprocessor.v1.service.LibraryServiceClient;
import lombok.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.reactive.function.BodyInserters.fromPublisher;
import static reactor.core.publisher.Mono.just;

/**
 * @author skmitra
 * @since Dec 11/12/21, 2021
 */
@Service
public class DefaultLibraryServiceClient implements LibraryServiceClient {

    public static final String LOCATION_HEADER = "Location";
    @NonNull
    private final WebClient client;

    public DefaultLibraryServiceClient(WebClient.Builder builder,
                                       RemoteServiceRegistry serviceRegistry,
                                       AuthenticationTokenExchangeFilterFunction filterFunction) {
        this.client = builder
                .baseUrl(serviceRegistry.getService("library-service").getBaseUrl())
                .filter(filterFunction)
                .defaultHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .build();
    }

    @Override
    public Mono<String> saveEBookSegment(EBookSegment segment) {
        return client
                .post()
                .uri("/api/v1/books/{bookId}/segments", segment.getBookId())
                .body(fromPublisher(just(segment), EBookSegment.class))
                .retrieve()
                .onStatus(status -> status == CONFLICT,
                        res -> Mono.error(() -> new EBookNotFoundException(segment.getBookId())))
                .toBodilessEntity()
                .map(entity -> entity.getHeaders().getFirst(LOCATION_HEADER));
    }
}
