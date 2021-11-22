package com.github.sujankumarmitra.libraryservice.v1.dao.impl;

import com.github.javafaker.Faker;
import com.github.sujankumarmitra.libraryservice.v1.dao.AuthorDao;
import com.github.sujankumarmitra.libraryservice.v1.dao.TagDao;
import com.github.sujankumarmitra.libraryservice.v1.dao.impl.entity.R2dbcBook;
import com.github.sujankumarmitra.libraryservice.v1.model.Book;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anySet;

/**
 * @author skmitra
 * @since Nov 22/11/21, 2021
 */
@DataR2dbcTest
@Testcontainers
@Slf4j
class R2dbcPostgresqlBookDaoTest {

    private R2dbcPostgresqlBookDao bookDao = null;
    @Autowired
    private R2dbcEntityTemplate entityTemplate = null;

    private Faker faker = new Faker();

    @Container
    private static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres");

    @DynamicPropertySource
    static void registerDynamicProperties(DynamicPropertyRegistry registry) {

        registry.add("spring.r2dbc.url", () ->
                postgreSQLContainer.getJdbcUrl().replace("jdbc", "r2dbc"));
        registry.add("spring.r2dbc.username", postgreSQLContainer::getUsername);
        registry.add("spring.r2dbc.password", postgreSQLContainer::getPassword);
    }

    @BeforeEach
    void setUp() {
        AuthorDao mockAuthorDao = Mockito.mock(AuthorDao.class);
        TagDao mockTagDao = Mockito.mock(TagDao.class);

        Mockito.doReturn(Mono.empty())
                .when(mockAuthorDao).insertAuthors(anySet());

        Mockito.doReturn(Mono.empty())
                .when(mockTagDao).insertTags(anySet());

        Mockito.doReturn(Mono.empty())
                .when(mockAuthorDao).updateAuthors(anySet());

        Mockito.doReturn(Mono.empty())
                .when(mockTagDao).updateTags(anySet());

        bookDao = new R2dbcPostgresqlBookDao(
                entityTemplate.getDatabaseClient(),
                entityTemplate.getDatabaseClient(),
                mockAuthorDao,
                mockTagDao);
    }

    @AfterEach
    void tearDown() {
        entityTemplate.getDatabaseClient()
                .sql("DELETE FROM books")
                .fetch()
                .rowsUpdated()
                .block();
    }

    @Test
    void givenValidBook_whenInsert_shouldInsert() {
        Book book = getBook();
        bookDao.insertBook(book)
                .as(StepVerifier::create)
                .expectSubscription()
                .consumeNextWith(log::info)
                .verifyComplete();
    }

    @Test
    void givenValidBook_whenUpdated_shouldUpdate() {

        R2dbcBook book = getBook();

        entityTemplate
                .getDatabaseClient()
                .sql(R2dbcPostgresqlBookDao.INSERT_STATEMENT)
                .bind("$1", book.getTitle())
                .bind("$2", book.getPublisher())
                .bind("$3", book.getEdition())
                .fetch()
                .one()
                .map(map -> map.get("id"))
                .cast(UUID.class)
                .doOnNext(book::setId)
                .then()
                .as(StepVerifier::create)
                .expectSubscription()
                .verifyComplete();

        log.info("Inserted book:: {}", book);

        String expectedTitle = faker.book().title();
        String expectedPublisher = faker.book().publisher();
        String expectedEdition = "2nd";

        book.setTitle(expectedTitle);
        book.setPublisher(expectedPublisher);
        book.setEdition(expectedEdition);

        log.info("Expected book:: {}", book);

        bookDao.updateBook(book)
                .then(Mono.defer(() -> entityTemplate
                        .getDatabaseClient()
                        .sql(R2dbcPostgresqlBookDao.SELECT_STATEMENT)
                        .bind("$1", book.getUuid())
                        .map(row -> {
                            R2dbcBook actualBook = new R2dbcBook();

                            actualBook.setId(book.getUuid());
                            actualBook.setTitle(row.get("title", String.class));
                            actualBook.setPublisher(row.get("publisher", String.class));
                            actualBook.setEdition(row.get("edition", String.class));

                            return actualBook;
                        })
                        .one()
                        .switchIfEmpty(Mono.error(new RuntimeException("no rows fetched")))))
                .as(StepVerifier::create)
                .consumeNextWith(actualBook -> {
                    log.info("Actual book:: {} ", actualBook);

                    String actualTitle = actualBook.getTitle();
                    String actualPublisher = actualBook.getPublisher();
                    String actualEdition = actualBook.getEdition();

                    assertThat(expectedTitle).isEqualTo(actualTitle);
                    assertThat(expectedEdition).isEqualTo(actualEdition);
                    assertThat(expectedPublisher).isEqualTo(actualPublisher);

                }).verifyComplete();
    }

    @Test
    void givenNonExistingBookId_whenUpdate_shouldUpdate() {
        R2dbcBook book = getBook();
        book.setId(UUID.randomUUID());

        bookDao.updateBook(book)
                .as(StepVerifier::create)
                .expectSubscription()
                .verifyComplete();
    }

    @Test
    void givenValidBookId_whenDelete_shouldDelete() {
        entityTemplate.getDatabaseClient()
                .sql(R2dbcPostgresqlBookDao.INSERT_STATEMENT)
                .bind("$1", faker.book().title())
                .bind("$2", faker.book().publisher())
                .bind("$3", "1st")
                .fetch()
                .one()
                .map(map -> map.get("id"))
                .cast(UUID.class)
                .map(Object::toString)
                .flatMap(bookDao::deleteBook)
                .as(StepVerifier::create)
                .expectSubscription()
                .verifyComplete();

        entityTemplate.getDatabaseClient()
                .sql("SELECT * FROM books")
                .fetch()
                .all()
                .count()
                .as(StepVerifier::create)
                .expectSubscription()
                .expectNext(0L)
                .verifyComplete();
    }

    @Test
    void givenInvalidId_whenDelete_shouldEmitEmptyMono() {
        bookDao.deleteBook(UUID.randomUUID().toString())
                .as(StepVerifier::create)
                .expectSubscription()
                .verifyComplete();
    }

    @Test
    void givenMalformedUuid_whenDelete_shouldEmitEmpty() {
        bookDao.deleteBook("malformed")
                .as(StepVerifier::create)
                .expectSubscription()
                .verifyComplete();
    }

    private R2dbcBook getBook() {

        R2dbcBook book = new R2dbcBook();
        book.setTitle(faker.book().title());
        book.setPublisher(faker.book().publisher());
        book.setEdition("1st");

        return book;

    }
}