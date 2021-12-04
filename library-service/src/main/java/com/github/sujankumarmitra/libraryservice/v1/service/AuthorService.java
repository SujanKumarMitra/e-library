package com.github.sujankumarmitra.libraryservice.v1.service;

import com.github.sujankumarmitra.libraryservice.v1.model.Author;
import reactor.core.publisher.Mono;

/**
 * @author skmitra
 * @since Dec 04/12/21, 2021
 */
public interface AuthorService {
    Mono<String> createAuthor(Author request);

    Mono<Void> updateAuthor(Author author);

    Mono<Void> deleteAuthor(String id);
}
