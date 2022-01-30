package com.github.sujankumarmitra.libraryservice.v1.dao;

import com.github.sujankumarmitra.libraryservice.v1.model.BookAuthor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;

/**
 * @author skmitra
 * @since Nov 21/11/21, 2021
 */
public interface BookAuthorDao {

    Flux<String> createAuthors(Collection<? extends BookAuthor> authors);

    Flux<BookAuthor> getAuthorsByBookId(String bookId);

    Mono<Void> deleteAuthorsByBookId(String bookId);

}
