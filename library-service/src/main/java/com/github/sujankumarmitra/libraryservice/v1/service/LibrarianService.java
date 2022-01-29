package com.github.sujankumarmitra.libraryservice.v1.service;

import com.github.sujankumarmitra.libraryservice.v1.model.Librarian;
import reactor.core.publisher.Mono;

/**
 * @author skmitra
 * @since Dec 09/12/21, 2021
 */
public interface LibrarianService {

    Mono<Void> addLibrarian(Librarian librarian);

    Mono<Void> deleteLibrarian(Librarian librarian);
}
