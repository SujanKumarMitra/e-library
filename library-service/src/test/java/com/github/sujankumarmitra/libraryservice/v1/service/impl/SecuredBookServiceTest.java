package com.github.sujankumarmitra.libraryservice.v1.service.impl;

import com.github.sujankumarmitra.libraryservice.LazySpringBootTest;
import com.github.sujankumarmitra.libraryservice.v1.dao.impl.entity.R2dbcEBook;
import com.github.sujankumarmitra.libraryservice.v1.dao.impl.entity.R2dbcPhysicalBook;
import com.github.sujankumarmitra.libraryservice.v1.model.EBook;
import com.github.sujankumarmitra.libraryservice.v1.model.PhysicalBook;
import com.github.sujankumarmitra.libraryservice.v1.service.BookService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;

/**
 * @author skmitra
 * @since Jan 26/01/22, 2022
 */
@Slf4j
@LazySpringBootTest
class SecuredBookServiceTest {

    @Autowired
    private BookService securedBookService;
    @MockBean
    private DefaultBookService mockBookService;

    @Test
    void shouldInjectBookServiceBean() {
        Assertions.assertNotNull(securedBookService);
    }


    @Test
    @WithMockUser(authorities = "library1:ROLE_LIBRARIAN")
    void givenValidUser_whenCreateBook_shouldCreateBook() {
        Mockito.doReturn(Mono.just("bookId"))
                .when(mockBookService).createBook((PhysicalBook) any());
        Mockito.doReturn(Mono.just("bookId"))
                .when(mockBookService).createBook((EBook) any());

        R2dbcPhysicalBook physicalBook = new R2dbcPhysicalBook();
        physicalBook.setLibraryId("library1");

        R2dbcEBook eBook = new R2dbcEBook();
        eBook.setLibraryId("library1");

        securedBookService
                .createBook(physicalBook)
                .concatWith(securedBookService.createBook(eBook))
                .as(StepVerifier::create)
                .expectSubscription()
                .expectNext("bookId")
                .expectNext("bookId")
                .expectComplete()
                .verify();

    }

    @Test
    @WithMockUser
    void givenUserWithNoAuthority_whenCreateBook_shouldEmitError() {

        R2dbcPhysicalBook physicalBook = new R2dbcPhysicalBook();
        physicalBook.setLibraryId("library1");

        securedBookService
                .createBook(physicalBook)
                .as(StepVerifier::create)
                .expectSubscription()
                .expectErrorSatisfies(err -> log.info("Thrown=", err))
                .verify();

    }

    @Test
    @WithMockUser(authorities = "library1:ROLE_LIBRARIAN")
    void givenUserWithAuthority_whenDeleteBook_shouldDeleteBook() {

        R2dbcPhysicalBook book = new R2dbcPhysicalBook();
        book.setLibraryId("library1");

        Mockito.doReturn(Mono.just(book))
                .when(mockBookService).getBook("book1");
        Mockito.doReturn(Mono.empty())
                .when(mockBookService).deleteBook(any());

        securedBookService
                .deleteBook("book1")
                .as(StepVerifier::create)
                .expectSubscription()
                .expectComplete()
                .verify();
    }

    @Test
    @WithMockUser(authorities = "library2:ROLE_LIBRARIAN")
    void givenUserWithNoAuthority_whenDeleteBook_shouldEmitError() {

        R2dbcPhysicalBook book = new R2dbcPhysicalBook();
        book.setLibraryId("library1");

        Mockito.doReturn(Mono.just(book))
                .when(mockBookService).getBook("book1");

        securedBookService
                .deleteBook("book1")
                .as(StepVerifier::create)
                .expectSubscription()
                .expectErrorSatisfies(err -> log.info("Thrown=", err))
                .verify();
    }

    @Test
    @WithMockUser(authorities = "library1:ROLE_STUDENT")
    void givenStudentUser_whenGetBook_shouldGetBook() {

        testGetBook();
    }

    @Test
    @WithMockUser(authorities = "library1:ROLE_LIBRARIAN")
    void givenLibrarianUser_whenGetBook_shouldGetBook() {
        testGetBook();
    }

    private void testGetBook() {
        UUID bookId = UUID.randomUUID();

        R2dbcPhysicalBook book = new R2dbcPhysicalBook();
        book.setLibraryId("library1");
        book.setId(bookId);

        Mockito.doReturn(Mono.just(book))
                .when(mockBookService).getBook(bookId.toString());


        securedBookService
                .getBook(bookId.toString())
                .as(StepVerifier::create)
                .expectSubscription()
                .expectNextCount(1L)
                .expectComplete()
                .verify();
    }

    @Test
    @WithMockUser
    void givenUserWithNoAuthority_whenGetBook_shouldEmitError() {

        UUID bookId = UUID.randomUUID();

        R2dbcPhysicalBook book = new R2dbcPhysicalBook();
        book.setLibraryId("library1");
        book.setId(bookId);

        Mockito.doReturn(Mono.just(book))
                .when(mockBookService).getBook(bookId.toString());

        securedBookService
                .getBook(bookId.toString())
                .as(StepVerifier::create)
                .expectSubscription()
                .expectErrorSatisfies(err -> log.info("Thrown=", err))
                .verify();
    }


    @Test
    @WithMockUser(authorities = "library1:ROLE_STUDENT")
    void givenStudentUser_whenGetBooks_shouldGetBooks() {

        testGetBooks();
    }

    @Test
    @WithMockUser(authorities = "library1:ROLE_LIBRARIAN")
    void givenLibraryUser_whenGetBooks_shouldGetBooks() {

        testGetBooks();
    }

    @Test
    @WithMockUser(authorities = "library2:ROLE_LIBRARIAN")
    void givenUserWithNoAuthorityOnGivenLibraryId_whenGetBook_shouldEmitError() {

        securedBookService
                .getBooks("library1",1)
                .as(StepVerifier::create)
                .expectSubscription()
                .expectErrorSatisfies(err -> log.info("Thrown=", err))
                .verify();
    }


    private void testGetBooks() {
        Mockito.doReturn(Flux.empty())
                .when(mockBookService).getBooks(eq("library1"), anyInt());

        securedBookService
                .getBooks("library1",1)
                .as(StepVerifier::create)
                .expectSubscription()
                .expectComplete()
                .verify();
    }
}