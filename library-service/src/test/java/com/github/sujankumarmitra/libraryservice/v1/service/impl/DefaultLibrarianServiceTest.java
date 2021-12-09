package com.github.sujankumarmitra.libraryservice.v1.service.impl;

import com.github.sujankumarmitra.libraryservice.v1.dao.LibrarianDao;
import com.github.sujankumarmitra.libraryservice.v1.exception.LibrarianAlreadyExistsException;
import com.github.sujankumarmitra.libraryservice.v1.model.Librarian;
import com.github.sujankumarmitra.libraryservice.v1.model.impl.DefaultLibrarian;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

/**
 * @author skmitra
 * @since Dec 09/12/21, 2021
 */
@ExtendWith(MockitoExtension.class)
class DefaultLibrarianServiceTest {

    @Mock
    LibrarianDao librarianDao;

    private DefaultLibrarianService librarianService;

    @BeforeEach
    void setUp() {
        librarianService = new DefaultLibrarianService(librarianDao);
    }


    @Test
    void givenDaoWhichThrowsExceptionWhenCreateLibrarian_whenSaveLibrarian_shouldSwallowErrorSignal() {

        Librarian librarian = new DefaultLibrarian(UUID.randomUUID().toString());

        Mockito.doReturn(Mono.error(new LibrarianAlreadyExistsException(librarian.getId())))
                .when(librarianDao).createLibrarian(librarian);


        librarianService
                .addLibrarian(librarian)
                .as(StepVerifier::create)
                .expectSubscription()
                .expectComplete()
                .verify();
    }
}