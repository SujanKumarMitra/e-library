package com.github.sujankumarmitra.libraryservice.v1.dao.impl;

import com.github.sujankumarmitra.libraryservice.v1.dao.impl.entity.R2dbcBookAuthor;
import com.github.sujankumarmitra.libraryservice.v1.exception.BookNotFoundException;
import com.github.sujankumarmitra.libraryservice.v1.model.BookAuthor;
import com.github.sujankumarmitra.libraryservice.v1.util.BookDaoTestUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.*;

import static com.github.sujankumarmitra.libraryservice.v1.util.DaoTestUtils.truncateAllTables;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author skmitra
 * @since Nov 23/11/21, 2021
 */
@Slf4j
class R2dbcPostgresqlBookAuthorDaoTest extends AbstractDataR2dbcPostgresqlContainerDependentTest {

    private R2dbcPostgresqlAuthorDao authorDao = null;
    @Autowired
    private R2dbcEntityTemplate entityTemplate = null;

    @BeforeEach
    void setUp() {
        authorDao = new R2dbcPostgresqlAuthorDao(entityTemplate.getDatabaseClient());
    }

    @AfterEach
    void tearDown() {
        truncateAllTables(entityTemplate.getDatabaseClient())
                .block();
    }

    @Test
    void givenValidBookId_whenInsert_ShouldInsert() {

        R2dbcBookAuthor author1 = new R2dbcBookAuthor();
        author1.setName("name1");

        R2dbcBookAuthor author2 = new R2dbcBookAuthor();
        author2.setName("name2");

        R2dbcBookAuthor author3 = new R2dbcBookAuthor();
        author3.setName("name3");

        Collection<R2dbcBookAuthor> authors = List.of(author1, author2, author3);


        BookDaoTestUtils.insertDummyBook(entityTemplate.getDatabaseClient())
                .doOnSuccess(book -> authors.forEach(author -> author.setBookId(book.getUuid())))
                .thenReturn(authors)
                .flatMapMany(authorDao::createAuthors)
                .then(
                        entityTemplate
                                .select(R2dbcBookAuthor.class)
                                .from("authors")
                                .all()
                                .count())
                .as(StepVerifier::create)
                .expectNext(3L)
                .verifyComplete();
    }

    @Test
    void givenNonExistingBookId_whenSelect_shouldEmitEmpty() {
        authorDao.getAuthorsByBookId(UUID.randomUUID().toString())
                .as(StepVerifier::create)
                .expectSubscription()
                .expectNextCount(0L)
                .verifyComplete();
    }

    @Test
    void givenMalformedUuid_whenSelect_shouldEmitEmpty() {
        authorDao.getAuthorsByBookId("malformed-uuid")
                .as(StepVerifier::create)
                .expectSubscription()
                .expectNextCount(0L)
                .verifyComplete();
    }

    @Test
    void givenMalformedUuidBookId_whenInsert_shouldEmitError() {

        Set<BookAuthor> tags = Set.of(new BookAuthor() {
            @Override
            public String getId() {
                return null;
            }

            @Override
            public String getBookId() {
                return "malformed";
            }

            @Override
            public String getName() {
                return "name";
            }

        });

        authorDao.createAuthors(tags)
                .as(StepVerifier::create)
                .expectSubscription()
                .expectError(BookNotFoundException.class)
                .verify();
    }

    @Test
    void givenNonExistingBookId_whenInsert_shouldEmitError() {
        R2dbcBookAuthor author1 = new R2dbcBookAuthor();
        author1.setBookId(UUID.randomUUID());
        author1.setName("name1");

        R2dbcBookAuthor author2 = new R2dbcBookAuthor();
        author2.setBookId(UUID.randomUUID());
        author2.setName("name2");

        Set<R2dbcBookAuthor> tags = Set.of(author1, author2);

        authorDao.createAuthors(tags)
                .as(StepVerifier::create)
                .expectSubscription()
                .expectErrorSatisfies(th -> {
                    assertThat(th).isExactlyInstanceOf(BookNotFoundException.class);
                    log.info("Exception errors:: {}", ((BookNotFoundException) th).getErrors());
                })
                .verify();
    }


    private void insertAuthors(List<R2dbcBookAuthor> authors) {
        BookDaoTestUtils
                .insertDummyBook(entityTemplate.getDatabaseClient())
                .doOnSuccess(book -> authors.forEach(author -> author.setBookId(book.getUuid())))
                .thenMany(Flux.fromIterable(authors))
                .flatMapSequential(author -> entityTemplate
                        .getDatabaseClient()
                        .sql(R2dbcPostgresqlAuthorDao.INSERT_STATEMENT)
                        .bind("$1", author.getBookUuid())
                        .bind("$2", author.getName())
                        .map(row -> row.get("id", UUID.class))
                        .all())
                .collectList()
                .doOnSuccess(authorIds -> {
                    Iterator<UUID> idIterator = authorIds.iterator();
                    Iterator<R2dbcBookAuthor> authorIterator = authors.iterator();

                    while (idIterator.hasNext()) {
                        authorIterator
                                .next()
                                .setId(idIterator.next());
                    }

                })
                .then()
                .as(StepVerifier::create)
                .verifyComplete();
    }


    @Test
    void givenValidBookId_whenDelete_shouldDelete() {
        List<R2dbcBookAuthor> authors = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            R2dbcBookAuthor author = new R2dbcBookAuthor();
            author.setName("name" + i);
            authors.add(author);
        }

        insertAuthors(authors);
        String bookId = authors.get(0).getBookId();

        authorDao
                .deleteAuthorsByBookId(bookId)
                .then(entityTemplate
                        .getDatabaseClient()
                        .sql("SELECT * from authors")
                        .fetch()
                        .all()
                        .count())
                .as(StepVerifier::create)
                .expectNext(0L)
                .verifyComplete();
    }

    @Test
    void givenNonExistingBookId_whenDelete_shouldEmitEmpty() {
        authorDao.deleteAuthorsByBookId(UUID.randomUUID().toString())
                .as(StepVerifier::create)
                .expectSubscription()
                .expectNextCount(0L)
                .verifyComplete();
    }

    @Test
    void givenMalformedUuid_whenDelete_shouldEmitEmpty() {
        authorDao.deleteAuthorsByBookId("malformed-uuid")
                .as(StepVerifier::create)
                .expectSubscription()
                .expectNextCount(0L)
                .verifyComplete();
    }
}