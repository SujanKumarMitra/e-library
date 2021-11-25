package com.github.sujankumarmitra.libraryservice.v1.dao;

import com.github.sujankumarmitra.libraryservice.v1.model.Author;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;

/**
 * @author skmitra
 * @since Nov 21/11/21, 2021
 */
public interface AuthorDao {

    Flux<String> createAuthors(Collection<? extends Author> authors);

    Flux<Author> getAuthorsByBookId(String bookId);

    Mono<Void> updateAuthors(Collection<? extends Author> authors);

    Mono<Void> deleteAuthorsByBookId(String bookId);
}
