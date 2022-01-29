package com.github.sujankumarmitra.libraryservice.v1.service;

import com.github.sujankumarmitra.libraryservice.v1.model.BookAuthor;
import reactor.core.publisher.Mono;

/**
 * @author skmitra
 * @since Dec 04/12/21, 2021
 */
public interface AuthorService {
    Mono<String> createAuthor(BookAuthor request);

    Mono<Void> updateAuthor(BookAuthor bookAuthor);

    Mono<Void> deleteAuthor(String id);
}
