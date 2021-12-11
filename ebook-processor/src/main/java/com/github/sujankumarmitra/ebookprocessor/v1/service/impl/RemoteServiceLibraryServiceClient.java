package com.github.sujankumarmitra.ebookprocessor.v1.service.impl;

import com.github.sujankumarmitra.ebookprocessor.v1.config.RemoteServiceRegistry;
import com.github.sujankumarmitra.ebookprocessor.v1.exception.EBookNotFoundException;
import com.github.sujankumarmitra.ebookprocessor.v1.model.EBook;
import com.github.sujankumarmitra.ebookprocessor.v1.model.EBookFormat;
import com.github.sujankumarmitra.ebookprocessor.v1.model.EBookSegment;
import com.github.sujankumarmitra.ebookprocessor.v1.security.AuthenticationTokenExchangeFilterFunction;
import com.github.sujankumarmitra.ebookprocessor.v1.service.LibraryServiceClient;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.reactive.function.BodyInserters.fromPublisher;
import static reactor.core.publisher.Mono.just;

/**
 * @author skmitra
 * @since Dec 11/12/21, 2021
 */
@Service
public class RemoteServiceLibraryServiceClient implements LibraryServiceClient {

    public static final String LOCATION_HEADER = "Location";
    public static final String EBOOK_SEGMENT_URI = "/api/v1/books/{bookId}/segments";
    @NonNull
    private final WebClient client;

    public RemoteServiceLibraryServiceClient(WebClient.Builder builder,
                                             RemoteServiceRegistry serviceRegistry,
                                             AuthenticationTokenExchangeFilterFunction filterFunction) {
        this.client = builder
                .baseUrl(serviceRegistry.getService("library-service").getBaseUrl())
                .filter(filterFunction)
                .defaultHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .defaultHeader(ACCEPT, APPLICATION_JSON_VALUE)
                .build();
    }

    @Override
    public Mono<EBook> getEBook(String bookId) {
        return client.get()
                .uri("/api/v1/books/{bookId}", bookId)
                .retrieve()
                .onStatus(status -> status == NOT_FOUND, res -> Mono.empty())
                .bodyToMono(GetEBookResponse.class)
                .filter(GetEBookResponse::isValidEBook)
                .cast(EBook.class);
    }

    @Override
    public Mono<String> saveEBookSegment(EBookSegment segment) {
        return client
                .post()
                .uri(EBOOK_SEGMENT_URI, segment.getBookId())
                .body(fromPublisher(just(segment), EBookSegment.class))
                .retrieve()
                .onStatus(status -> status == CONFLICT,
                        res -> Mono.error(() -> new EBookNotFoundException(segment.getBookId())))
                .toBodilessEntity()
                .map(entity -> entity.getHeaders().getFirst(LOCATION_HEADER));
    }

    @Override
    public Mono<Void> deleteEBookSegments(String bookId) {
        return client
                .delete()
                .uri(EBOOK_SEGMENT_URI, bookId)
                .retrieve()
                .toBodilessEntity()
                .then();
    }

    @Getter
    @Setter
    protected static class GetEBookResponse extends EBook {
        public static final String BOOK_TYPE = "EBOOK";

        private String id;
        private String type;
        private EBookFormat format;

        public boolean isValidEBook() {
            return type != null && type.equals(BOOK_TYPE);
        }

        @Override
        public boolean equals(Object o) {
            return super.equals(o);
        }

        @Override
        public int hashCode() {
            return super.hashCode();
        }

    }
}
