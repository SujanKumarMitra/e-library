package com.github.sujankumarmitra.ebookprocessor.v1.service.impl;

import com.github.sujankumarmitra.ebookprocessor.v1.config.RemoteServiceRegistry;
import com.github.sujankumarmitra.ebookprocessor.v1.exception.EBookNotFoundException;
import com.github.sujankumarmitra.ebookprocessor.v1.model.EBook;
import com.github.sujankumarmitra.ebookprocessor.v1.model.EBookFormat;
import com.github.sujankumarmitra.ebookprocessor.v1.security.AuthenticationTokenExchangeFilterFunction;
import com.github.sujankumarmitra.ebookprocessor.v1.service.EBookService;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * @author skmitra
 * @since Dec 11/12/21, 2021
 */
@Service
@Slf4j
public class RemoteServiceEBookService implements EBookService {
    @NonNull
    private final WebClient client;

    public RemoteServiceEBookService(WebClient.Builder builder,
                                     RemoteServiceRegistry serviceRegistry,
                                     AuthenticationTokenExchangeFilterFunction filterFunction) {
        String baseUrl = serviceRegistry.getService("library-service").getBaseUrl();
        this.client = builder.baseUrl(baseUrl)
                .filter(filterFunction)
                .defaultHeader(ACCEPT, APPLICATION_JSON_VALUE)
                .build();
    }

    @Override
    public Mono<EBook> getEBook(String ebookId) {
        return client.get()
                .uri("/api/v1/books/{bookId}", ebookId)
                .retrieve()
                .onStatus(status -> status == NOT_FOUND, res-> Mono.error(() -> new EBookNotFoundException(ebookId)))
                .bodyToMono(GetEBookResponse.class)
                .filter(GetEBookResponse::isValidEBook)
                .switchIfEmpty(Mono.error(() -> new EBookNotFoundException(ebookId)))
                .cast(EBook.class);
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
