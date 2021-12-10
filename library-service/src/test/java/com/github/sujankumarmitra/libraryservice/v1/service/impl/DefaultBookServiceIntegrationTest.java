package com.github.sujankumarmitra.libraryservice.v1.service.impl;

import com.github.sujankumarmitra.libraryservice.AbstractSystemTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.springframework.r2dbc.connection.init.ScriptUtils.executeSqlScript;

/**
 * @author skmitra
 * @since Dec 10/12/21, 2021
 */
@Slf4j
class DefaultBookServiceIntegrationTest extends AbstractSystemTest {

    @Autowired
    private DefaultBookService bookService;
    @Autowired
    private R2dbcEntityTemplate entityTemplate;
    @Value("classpath:sample_data.sql")
    private Resource sampleData;

    @Test
    void testDeleteBookId() {

        UUID validPhysicalBookId = UUID.fromString("d4c608c4-7ac6-48b9-b90d-9952e798578b");
        UUID validEBookId = UUID.fromString("60dc01ad-43be-4501-aaa8-3c12741dff4f");

        entityTemplate
                .getDatabaseClient()
                .inConnection(conn -> executeSqlScript(conn, sampleData))
                .as(StepVerifier::create)
                .expectSubscription()
                .expectComplete()
                .verify();

        bookService
                .deleteBook(validPhysicalBookId.toString())
                .then(entityTemplate
                        .getDatabaseClient()
                        .sql("SELECT COUNT(*) from physical_books WHERE book_id=$1")
                        .bind("$1", validPhysicalBookId)
                        .map(row -> row.get(0, Integer.class))
                        .one())
                .as(StepVerifier::create)
                .expectSubscription()
                .expectNext(0)
                .expectComplete()
                .verify();

        bookService
                .deleteBook(validEBookId.toString())
                .then(entityTemplate
                        .getDatabaseClient()
                        .sql("SELECT COUNT(*) from ebooks WHERE book_id=$1")
                        .bind("$1", validEBookId)
                        .map(row -> row.get(0, Integer.class))
                        .one())
                .as(StepVerifier::create)
                .expectSubscription()
                .expectNext(0)
                .expectComplete()
                .verify();
    }
}