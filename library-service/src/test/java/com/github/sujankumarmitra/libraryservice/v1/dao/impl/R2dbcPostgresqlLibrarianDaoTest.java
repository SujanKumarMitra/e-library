package com.github.sujankumarmitra.libraryservice.v1.dao.impl;

import com.github.sujankumarmitra.libraryservice.v1.exception.LibrarianAlreadyExistsException;
import com.github.sujankumarmitra.libraryservice.v1.model.Librarian;
import com.github.sujankumarmitra.libraryservice.v1.model.impl.DefaultLibrarian;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

/**
 * @author skmitra
 * @since Dec 09/12/21, 2021
 */
@Slf4j
class R2dbcPostgresqlLibrarianDaoTest extends AbstractDataR2dbcPostgreSQLContainerDependentTest {

    private R2dbcPostgresqlLibrarianDao librarianDao;
    @Autowired
    private R2dbcEntityTemplate entityTemplate;

    @BeforeEach
    void setUp() {
        librarianDao = new R2dbcPostgresqlLibrarianDao(entityTemplate);
    }

    @AfterEach
    void tearDown() {
        entityTemplate
                .getDatabaseClient()
                .sql("DELETE FROM librarians")
                .fetch()
                .rowsUpdated()
                .block();
    }

    @Test
    void givenValidLibrarian_whenCreate_shouldCreate() {
        Librarian librarian = new DefaultLibrarian(UUID.randomUUID().toString());

        librarianDao
                .createLibrarian(librarian)
                .then(getLibrarianCount())
                .as(StepVerifier::create)
                .expectSubscription()
                .expectNext(1)
                .expectComplete()
                .verify();
    }

    @Test
    void givenLibrarianWithExistingId_whenCreateLibrarian_shouldEmitError() {
        Librarian librarian = new DefaultLibrarian(UUID.randomUUID().toString());

        entityTemplate
                .getDatabaseClient()
                .sql(R2dbcPostgresqlLibrarianDao.INSERT_STATEMENT)
                .bind("$1", librarian.getId())
                .fetch()
                .rowsUpdated()
                .then(Mono.defer(() -> librarianDao.createLibrarian(librarian)))
                .as(StepVerifier::create)
                .expectSubscription()
                .expectError(LibrarianAlreadyExistsException.class)
                .verify();
    }

    @NonNull
    private Mono<Integer> getLibrarianCount() {
        return entityTemplate
                .getDatabaseClient()
                .sql("SELECT COUNT(*) as count FROM librarians")
                .map(row -> row.get("count", Integer.class))
                .one();
    }

    @Test
    void givenValidLibrarian_whenDelete_shouldDelete() {

        String librarianId = UUID.randomUUID().toString();

        entityTemplate
                .getDatabaseClient()
                .sql(R2dbcPostgresqlLibrarianDao.INSERT_STATEMENT)
                .bind("$1", librarianId)
                .fetch()
                .rowsUpdated()
                .then(Mono.defer(() -> librarianDao.deleteLibrarian(librarianId)))
                .then(getLibrarianCount())
                .as(StepVerifier::create)
                .expectSubscription()
                .expectNext(0)
                .expectComplete()
                .verify();
    }

    @Test
    void givenNonExistingLibrarianId_whenDelete_shouldEmitEmpty() {

        String librarianId = UUID.randomUUID().toString();

        librarianDao
                .deleteLibrarian(librarianId)
                .as(StepVerifier::create)
                .expectSubscription()
                .expectComplete()
                .verify();
    }
}
