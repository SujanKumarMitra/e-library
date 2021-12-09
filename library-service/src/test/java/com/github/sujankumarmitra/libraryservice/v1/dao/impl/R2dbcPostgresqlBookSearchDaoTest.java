package com.github.sujankumarmitra.libraryservice.v1.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import reactor.test.StepVerifier;

import static org.springframework.r2dbc.connection.init.ScriptUtils.executeSqlScript;

/**
 * @author skmitra
 * @since Dec 09/12/21, 2021
 */
@Slf4j
class R2dbcPostgresqlBookSearchDaoTest extends AbstractDataR2dbcPostgreSQLContainerDependentTest {

    @Autowired
    private R2dbcEntityTemplate entityTemplate;
    R2dbcPostgresqlBookSearchDao bookSearchDao;

    @BeforeEach
    void setUp() {
        bookSearchDao = new R2dbcPostgresqlBookSearchDao(entityTemplate);
        entityTemplate
                .getDatabaseClient()
                .inConnection(connection -> executeSqlScript(connection, new ClassPathResource("sample_data.sql")))
                .block();
    }

    @AfterEach
    void tearDown() {
        entityTemplate
                .getDatabaseClient()
                .sql("TRUNCATE TABLE books CASCADE")
                .fetch()
                .rowsUpdated()
                .block();
    }

    @Test
    void givenSetOfBooks_whenGetBookIds_shouldGetBookIds() {
        bookSearchDao
                .getBookIds(0,10)
                .as(StepVerifier::create)
                .expectSubscription()
                .expectNextCount(5L)
                .expectComplete()
                .verify();
    }

    @Test
    void givenSetOfBooks_whenGetBookIdsByTitleAndAuthor_shouldGetBookIds() {
        bookSearchDao
                .getBookIdsByTitleAndAuthorStartingWith("Lad","Fat",0,10)
                .as(StepVerifier::create)
                .expectSubscription()
                .expectNextCount(2L)
                .expectComplete()
                .verify();
    }

}