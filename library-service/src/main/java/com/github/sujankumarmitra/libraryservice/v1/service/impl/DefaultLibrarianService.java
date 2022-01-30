package com.github.sujankumarmitra.libraryservice.v1.service.impl;

import com.github.sujankumarmitra.libraryservice.v1.dao.LibrarianDao;
import com.github.sujankumarmitra.libraryservice.v1.exception.LibrarianAlreadyExistsException;
import com.github.sujankumarmitra.libraryservice.v1.model.Librarian;
import com.github.sujankumarmitra.libraryservice.v1.service.LibrarianService;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * @author skmitra
 * @since Dec 09/12/21, 2021
 */
@Service
@Slf4j
@AllArgsConstructor
public class DefaultLibrarianService implements LibrarianService {

    @NonNull
    private final LibrarianDao librarianDao;

    @Override
    public Mono<Void> addLibrarian(@NonNull Librarian librarian) {
        return librarianDao
                .createLibrarian(librarian)
                .onErrorResume(LibrarianAlreadyExistsException.class,
                        err -> Mono.fromRunnable(() -> log.info("Librarian with id '{}' already exists.", librarian.getUserId())));
    }

    @Override
    public Mono<Void> deleteLibrarian(Librarian librarian) {
        return librarianDao
                .deleteLibrarian(librarian);
    }
}
