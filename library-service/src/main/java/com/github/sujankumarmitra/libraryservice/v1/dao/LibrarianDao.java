package com.github.sujankumarmitra.libraryservice.v1.dao;

import com.github.sujankumarmitra.libraryservice.v1.model.Librarian;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author skmitra
 * @since Dec 09/12/21, 2021
 */
public interface LibrarianDao {
    Mono<Void> createLibrarian(Librarian librarian);

    <R extends Librarian> Flux<R> getLibrarians(String libraryId);

    Mono<Void> deleteLibrarian(Librarian librarian);
}
