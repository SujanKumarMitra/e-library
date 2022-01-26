package com.github.sujankumarmitra.libraryservice.v1.service.impl;

import com.github.sujankumarmitra.libraryservice.v1.config.DefaultPagingProperties;
import com.github.sujankumarmitra.libraryservice.v1.dao.BookDao;
import com.github.sujankumarmitra.libraryservice.v1.dao.PackageDao;
import com.github.sujankumarmitra.libraryservice.v1.dao.impl.entity.R2dbcBook;
import com.github.sujankumarmitra.libraryservice.v1.dao.impl.entity.R2dbcPackage;
import com.github.sujankumarmitra.libraryservice.v1.dao.impl.entity.R2dbcPackageItem;
import com.github.sujankumarmitra.libraryservice.v1.exception.IncorrectLibraryIdException;
import com.github.sujankumarmitra.libraryservice.v1.model.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Set;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;

/**
 * @author skmitra
 * @since Jan 26/01/22, 2022
 */
@ExtendWith(MockitoExtension.class)
class DefaultPackageServiceTest {

    private DefaultPackageService packageService;
    @Mock
    private PackageDao dao;
    @Mock
    private BookDao<Book> bookDao;
    @Mock
    private DefaultPagingProperties pagingProperties;

    @BeforeEach
    void setUp() {
        packageService = new DefaultPackageService(dao, bookDao, pagingProperties);
    }

    @Test
    void givenIncorrectLibraryId_whenCreatePackage_shouldEmitError() {

        UUID bookId = UUID.randomUUID();
        UUID packageId = UUID.randomUUID();

        Mockito.doAnswer(invocation -> {
            R2dbcBook book1 = new R2dbcBook();
            book1.setId(bookId);
            book1.setLibraryId("library1");

            return Mono.just(book1);
        }).when(bookDao).getBook(bookId.toString());

        R2dbcPackageItem bookItem = new R2dbcPackageItem();
        bookItem.setPackageId(packageId);
        bookItem.setBookId(bookId);

        R2dbcPackage aPackage = new R2dbcPackage();
        aPackage.setName("packageName");
        aPackage.setLibraryId("library2");
        aPackage.setItems(Set.of(bookItem));


        packageService
                .createPackage(aPackage)
                .as(StepVerifier::create)
                .expectSubscription()
                .expectError(IncorrectLibraryIdException.class)
                .verify();
    }

    @Test
    void givenIncorrectLibraryId_whenUpdatePackage_shouldEmitError() {

        UUID bookId = UUID.randomUUID();
        UUID packageId = UUID.randomUUID();

        Mockito.doAnswer(invocation -> {
            R2dbcBook book1 = new R2dbcBook();
            book1.setId(bookId);
            book1.setLibraryId("library1");

            return Mono.just(book1);
        }).when(bookDao).getBook(bookId.toString());

        R2dbcPackageItem bookItem = new R2dbcPackageItem();
        bookItem.setPackageId(packageId);
        bookItem.setBookId(bookId);

        R2dbcPackage aPackage = new R2dbcPackage();
        aPackage.setLibraryId("library2");
        aPackage.setItems(Set.of(bookItem));


        packageService
                .updatePackage(aPackage)
                .as(StepVerifier::create)
                .expectSubscription()
                .expectError(IncorrectLibraryIdException.class)
                .verify();
    }

    @Test
    void givenCorrectLibraryId_whenCreatePackage_shouldEmitNewPackageId() {

        UUID bookId = UUID.randomUUID();
        UUID packageId = UUID.randomUUID();

        Mockito.doAnswer(invocation -> {
            R2dbcBook book1 = new R2dbcBook();
            book1.setId(bookId);
            book1.setLibraryId("library1");

            return Mono.just(book1);
        }).when(bookDao).getBook(bookId.toString());

        Mockito.doReturn(Mono.just("leaseRequest1")).when(dao).createPackage(any());

        R2dbcPackageItem bookItem = new R2dbcPackageItem();
        bookItem.setPackageId(packageId);
        bookItem.setBookId(bookId);

        R2dbcPackage aPackage = new R2dbcPackage();
        aPackage.setName("packageName");
        aPackage.setLibraryId("library1");
        aPackage.setItems(Set.of(bookItem));


        packageService
                .createPackage(aPackage)
                .as(StepVerifier::create)
                .expectSubscription()
                .expectNext("leaseRequest1")
                .expectComplete()
                .verify();
    }

    @Test
    void givenCorrectLibraryId_whenUpdatePackage_shouldEmitNoError() {

        UUID bookId = UUID.randomUUID();
        UUID packageId = UUID.randomUUID();

        Mockito.doAnswer(invocation -> {
            R2dbcBook book1 = new R2dbcBook();
            book1.setId(bookId);
            book1.setLibraryId("library1");

            return Mono.just(book1);
        }).when(bookDao).getBook(bookId.toString());

        Mockito.doReturn(Mono.empty()).when(dao).createPackage(any());

        R2dbcPackageItem bookItem = new R2dbcPackageItem();
        bookItem.setPackageId(packageId);
        bookItem.setBookId(bookId);

        R2dbcPackage aPackage = new R2dbcPackage();
        aPackage.setLibraryId("library1");
        aPackage.setItems(Set.of(bookItem));


        packageService
                .createPackage(aPackage)
                .as(StepVerifier::create)
                .expectSubscription()
                .expectComplete()
                .verify();
    }
}