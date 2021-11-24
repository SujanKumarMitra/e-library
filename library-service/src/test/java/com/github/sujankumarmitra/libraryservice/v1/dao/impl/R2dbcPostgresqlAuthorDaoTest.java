package com.github.sujankumarmitra.libraryservice.v1.dao.impl;

import com.github.sujankumarmitra.libraryservice.v1.dao.impl.entity.R2dbcAuthor;
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
import reactor.test.StepVerifier;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
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
                .delete(R2dbcAuthor.class)
                .from("authors")
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
        Set<R2dbcAuthor> expectedAuthors = new HashSet<>();

        R2dbcAuthor author1 = new R2dbcAuthor();
        author1.setName("name1");

        R2dbcAuthor author2 = new R2dbcAuthor();
        author2.setName("name1");

        R2dbcAuthor author3 = new R2dbcAuthor();
        author3.setName("name1");

        expectedAuthors.add(author1);
        expectedAuthors.add(author2);
        expectedAuthors.add(author3);


        BookDaoUtils.insertDummyBook(entityTemplate.getDatabaseClient())
                .doOnSuccess(book -> expectedAuthors.forEach(author -> author.setBookId(book.getUuid())))
                .thenReturn(expectedAuthors)
                .flatMap(authorDao::insertAuthors)
                .then(
                        entityTemplate
                                .select(R2dbcAuthor.class)
                                .from("authors")
                                .all()
                                .collect(Collectors.toCollection(HashSet::new)))
                .as(StepVerifier::create)
                .consumeNextWith(actualAuthors -> {
                    log.info("Expected Authors {}", expectedAuthors);
                    log.info("Actual Authors {}", actualAuthors);

                    assertThat(actualAuthors).isEqualTo(expectedAuthors);
                })
                .verifyComplete();
    }

    @Test
    void givenNonExistingBookId_whenSelect_shouldEmitEmpty() {
        authorDao.selectAuthors(UUID.randomUUID().toString())
                .as(StepVerifier::create)
                .expectSubscription()
                .expectNextCount(0L)
                .verifyComplete();
    }

    @Test
    void givenMalformedUuid_whenSelect_shouldEmitEmpty() {
        authorDao.selectAuthors("malformed-uuid")
                .as(StepVerifier::create)
                .expectSubscription()
                .expectNextCount(0L)
                .verifyComplete();
    }

    @Test
    void givenMalformedUuidBookId_whenInsert_shouldEmitError() {

        Set<Author> tags = Set.of(new Author() {
            @Override
            public String getBookId() {
                return "malformed";
            }

            @Override
            public String getName() {
                return "name";
            }

        });

        authorDao.insertAuthors(tags)
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

        authorDao.insertAuthors(tags)
                .as(StepVerifier::create)
                .expectSubscription()
                .expectErrorSatisfies(th -> {
                    assertThat(th).isExactlyInstanceOf(BookNotFoundException.class);
                    log.info("Exception errors:: {}", ((BookNotFoundException) th).getErrors());
                })
                .verify();
    }
//    @Test
//    void givenValidBookId_whenUpdateShouldUpdate() {
//        List<R2dbcTag> tags = new ArrayList<>();
//        for (int i = 1; i <= 10; i++) {
//            R2dbcTag tag = new R2dbcTag();
//
//            tag.setKey("key" + i);
//            tag.setValue("value" + i);
//
//            tags.add(tag);
//        }
//
//        insertTags(tags);
//
//        R2dbcTag tag1 = tags.get(2);
//        tag1.setValue("value33");
//
//        R2dbcTag tag2 = tags.get(5);
//        tag2.setValue("value66");
//
//        R2dbcTag tag3 = tags.get(8);
//        tag3.setValue("value99");
//
//        Set<R2dbcTag> expectedTags = new HashSet<>(tags);
//
//        Set<R2dbcTag> tagsToUpdate = new LinkedHashSet<>();
//        tagsToUpdate.add(tag1);
//        tagsToUpdate.add(tag2);
//        tagsToUpdate.add(tag3);
//
//        authorDao.updateTags(tagsToUpdate)
//                .thenMany(entityTemplate
//                        .select(R2dbcTag.class)
//                        .from("tags")
//                        .all())
//                .collect(Collectors.toSet())
//                .as(StepVerifier::create)
//                .consumeNextWith(actualTags -> {
//                    log.info("Expected:: {}", expectedTags);
//                    log.info("Actual:: {}", actualTags);
//
//                    assertThat(actualTags).isEqualTo(expectedTags);
//                })
//                .verifyComplete();
//
//    }
//
//    private void insertTags(List<R2dbcTag> tags) {
//        BookDaoUtils.insertDummyBook(entityTemplate.getDatabaseClient())
//                .doOnSuccess(book -> tags.forEach(tag -> tag.setBookId(book.getUuid())))
//                .thenMany(Flux.fromIterable(tags))
//                .flatMap(tag -> entityTemplate
//                        .getDatabaseClient()
//                        .sql(R2dbcPostgresqlTagDao.UPSERT_STATEMENT)
//                        .bind("$1", tag.getBookUuid())
//                        .bind("$2", tag.getKey())
//                        .bind("$3", tag.getValue())
//                        .fetch()
//                        .rowsUpdated())
//                .as(StepVerifier::create)
//                .expectNextCount(tags.size())
//                .verifyComplete();
//    }


}