package com.github.sujankumarmitra.libraryservice.v1.dao.impl;

import com.github.sujankumarmitra.libraryservice.v1.dao.impl.entity.R2dbcAuthor;
import com.github.sujankumarmitra.libraryservice.v1.dao.impl.entity.R2dbcBook;
import com.github.sujankumarmitra.libraryservice.v1.exception.BookNotFoundException;
import com.github.sujankumarmitra.libraryservice.v1.model.Author;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author skmitra
 * @since Nov 23/11/21, 2021
 */
@DataR2dbcTest
@Testcontainers
@Slf4j
class R2dbcPostgresqlAuthorDaoTest {

    private R2dbcPostgresqlAuthorDao authorDao = null;
    @Autowired
    private R2dbcEntityTemplate entityTemplate = null;

    @Container
    private static final PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres");

    @BeforeEach
    void setUp() {
        authorDao = new R2dbcPostgresqlAuthorDao(entityTemplate.getDatabaseClient());
    }

    @AfterEach
    void tearDown() {
        entityTemplate
                .delete(R2dbcAuthor.class)
                .from("authors")
                .all()
                .block();

        entityTemplate
                .delete(R2dbcBook.class)
                .from("books")
                .all()
                .block();
    }

    @DynamicPropertySource
    static void registerDynamicProperties(DynamicPropertyRegistry registry) {

        registry.add("spring.r2dbc.url", () ->
                postgreSQLContainer.getJdbcUrl().replace("jdbc", "r2dbc"));
        registry.add("spring.r2dbc.username", postgreSQLContainer::getUsername);
        registry.add("spring.r2dbc.password", postgreSQLContainer::getPassword);

    }


    @Test
    void givenValidBookId_whenInsert_ShouldInsert() {

        R2dbcAuthor author1 = new R2dbcAuthor();
        author1.setName("name1");

        R2dbcAuthor author2 = new R2dbcAuthor();
        author2.setName("name2");

        R2dbcAuthor author3 = new R2dbcAuthor();
        author3.setName("name3");

        Collection<R2dbcAuthor> authors = List.of(author1, author2, author3);


        BookDaoTestUtils.insertDummyBook(entityTemplate.getDatabaseClient())
                .doOnSuccess(book -> authors.forEach(author -> author.setBookId(book.getUuid())))
                .thenReturn(authors)
                .flatMapMany(authorDao::createAuthors)
                .then(
                        entityTemplate
                                .select(R2dbcAuthor.class)
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

        Set<Author> tags = Set.of(new Author() {
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
        R2dbcAuthor author1 = new R2dbcAuthor();
        author1.setBookId(UUID.randomUUID());
        author1.setName("name1");

        R2dbcAuthor author2 = new R2dbcAuthor();
        author2.setBookId(UUID.randomUUID());
        author2.setName("name2");

        Set<R2dbcAuthor> tags = Set.of(author1, author2);

        authorDao.createAuthors(tags)
                .as(StepVerifier::create)
                .expectSubscription()
                .expectErrorSatisfies(th -> {
                    assertThat(th).isExactlyInstanceOf(BookNotFoundException.class);
                    log.info("Exception errors:: {}", ((BookNotFoundException) th).getErrors());
                })
                .verify();
    }

    @Test
    void givenValidBookId_whenUpdate_shouldUpdate() {
        List<R2dbcAuthor> authors = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            R2dbcAuthor author = new R2dbcAuthor();
            author.setName("name" + i);
            authors.add(author);
        }

        insertAuthors(authors);

        R2dbcAuthor tag1 = authors.get(2);
        tag1.setName("value33");

        R2dbcAuthor tag2 = authors.get(5);
        tag2.setName("value66");

        R2dbcAuthor tag3 = authors.get(8);
        tag3.setName("value99");

        Set<R2dbcAuthor> expectedAuthors = new HashSet<>(authors);

        Set<R2dbcAuthor> tagsToUpdate = new LinkedHashSet<>();
        tagsToUpdate.add(tag1);
        tagsToUpdate.add(tag2);
        tagsToUpdate.add(tag3);

        authorDao.updateAuthors(tagsToUpdate)
                .thenMany(entityTemplate
                        .select(R2dbcAuthor.class)
                        .from("authors")
                        .all())
                .collect(Collectors.toSet())
                .as(StepVerifier::create)
                .consumeNextWith(actualTags -> {
                    log.info("Expected:: {}", expectedAuthors);
                    log.info("Actual:: {}", actualTags);

                    assertThat(actualTags).isEqualTo(expectedAuthors);
                })
                .verifyComplete();

    }

    private void insertAuthors(List<R2dbcAuthor> authors) {
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
                    Iterator<R2dbcAuthor> authorIterator = authors.iterator();

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
        List<R2dbcAuthor> authors = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            R2dbcAuthor author = new R2dbcAuthor();
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