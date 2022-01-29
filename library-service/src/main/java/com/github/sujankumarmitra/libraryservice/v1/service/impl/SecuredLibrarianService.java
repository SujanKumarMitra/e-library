package com.github.sujankumarmitra.libraryservice.v1.service.impl;

import com.github.sujankumarmitra.libraryservice.v1.model.Librarian;
import com.github.sujankumarmitra.libraryservice.v1.service.LibrarianService;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import static com.github.sujankumarmitra.libraryservice.v1.security.SecurityConstants.ROLE_ADMIN;

/**
 * @author skmitra
 * @since Jan 29/01/22, 2022
 */
@Service
@Primary
@AllArgsConstructor
public class SecuredLibrarianService implements LibrarianService {

    private final LibrarianService delegate;

    @Override
    @PreAuthorize("hasAuthority(#librarian.libraryId + ':" + ROLE_ADMIN + "')")
    public Mono<Void> addLibrarian(Librarian librarian) {
        return delegate.addLibrarian(librarian);
    }

    @Override
    @PreAuthorize("hasAuthority(#librarian.libraryId + ':" + ROLE_ADMIN + "')")
    public Mono<Void> deleteLibrarian(Librarian librarian) {
        return delegate.deleteLibrarian(librarian);
    }
}
