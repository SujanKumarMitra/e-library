package com.github.sujankumarmitra.libraryservice.v1.dao.impl;

import com.github.sujankumarmitra.libraryservice.v1.dao.impl.entity.R2dbcBook;
import com.github.sujankumarmitra.libraryservice.v1.dao.impl.entity.R2dbcTag;
import com.github.sujankumarmitra.libraryservice.v1.exception.BookNotFoundException;
import com.github.sujankumarmitra.libraryservice.v1.exception.DuplicateTagKeyException;
import com.github.sujankumarmitra.libraryservice.v1.model.Tag;
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
class R2dbcPostgresqlTagDaoTest {

    private R2dbcPostgresqlTagDao tagDao = null;
    @Autowired
    private R2dbcEntityTemplate entityTemplate = null;

    @Container
    private static final PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres");

    @BeforeEach
    void setUp() {
        tagDao = new R2dbcPostgresqlTagDao(entityTemplate.getDatabaseClient());
    }

    @AfterEach
    void tearDown() {
        entityTemplate
                .delete(R2dbcTag.class)
                .from("tags")
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

        R2dbcTag tag1 = new R2dbcTag();
        tag1.setKey("key1");
        tag1.setValue("value1");

        R2dbcTag tag2 = new R2dbcTag();
        tag2.setKey("key2");
        tag2.setValue("value2");

        R2dbcTag tag3 = new R2dbcTag();
        tag3.setKey("key3");
        tag3.setValue("value3");

        Collection<R2dbcTag> tags = List.of(tag1, tag2, tag3);


        BookDaoTestUtils.insertDummyBook(entityTemplate.getDatabaseClient())
                .doOnSuccess(book -> tags.forEach(tag -> tag.setBookId(book.getUuid())))
                .thenReturn(tags)
                .flatMapMany(tagDao::createTags)
                .then(entityTemplate
                        .select(R2dbcTag.class)
                        .from("tags")
                        .all()
                        .count())
                .as(StepVerifier::create)
                .expectNext(3L)
                .verifyComplete();
    }

    @Test
    void givenDuplicateTagKeys_whenInsert_ShouldEmitError() {

        R2dbcTag tag1 = new R2dbcTag();
        tag1.setKey("key1");
        tag1.setValue("value1");

        R2dbcTag tag2 = new R2dbcTag();
        tag2.setKey("key1");
        tag2.setValue("value2");

        Collection<R2dbcTag> tags = List.of(tag1, tag2);


        BookDaoTestUtils.insertDummyBook(entityTemplate.getDatabaseClient())
                .doOnSuccess(book -> tags.forEach(tag -> tag.setBookId(book.getUuid())))
                .thenReturn(tags)
                .flatMapMany(tagDao::createTags)
                .then(entityTemplate
                        .select(R2dbcTag.class)
                        .from("tags")
                        .all()
                        .count())
                .as(StepVerifier::create)
                .expectError(DuplicateTagKeyException.class)
                .verify();
    }

    @Test
    void givenNonExistingBookId_whenSelect_shouldEmitEmpty() {
        tagDao.getTagsByBookId(UUID.randomUUID().toString())
                .as(StepVerifier::create)
                .expectSubscription()
                .expectNextCount(0L)
                .verifyComplete();
    }

    @Test
    void givenMalformedUuid_whenSelect_shouldEmitEmpty() {
        tagDao.getTagsByBookId("malformed-uuid")
                .as(StepVerifier::create)
                .expectSubscription()
                .expectNextCount(0L)
                .verifyComplete();
    }

    @Test
    void givenMalformedUuidBookId_whenInsert_shouldEmitError() {

        Set<Tag> tags = Set.of(new Tag() {
            @Override
            public String getId() {
                return null;
            }

            @Override
            public String getBookId() {
                return "malformed";
            }

            @Override
            public String getKey() {
                return "key";
            }

            @Override
            public String getValue() {
                return "value";
            }

        });

        tagDao.createTags(tags)
                .as(StepVerifier::create)
                .expectSubscription()
                .expectError(BookNotFoundException.class)
                .verify();
    }

    @Test
    void givenNonExistingBookId_whenInsert_shouldEmitError() {
        R2dbcTag tag1 = new R2dbcTag();
        tag1.setBookId(UUID.randomUUID());
        tag1.setKey("key1");
        tag1.setValue("value1");

        R2dbcTag tag2 = new R2dbcTag();
        tag2.setBookId(UUID.randomUUID());
        tag2.setKey("key2");
        tag2.setValue("value2");

        Set<R2dbcTag> tags = Set.of(tag1, tag2);

        tagDao.createTags(tags)
                .as(StepVerifier::create)
                .expectSubscription()
                .expectErrorSatisfies(th -> {
                    assertThat(th).isExactlyInstanceOf(BookNotFoundException.class);
                    log.info("Exception errors:: {}", ((BookNotFoundException) th).getErrors());
                })
                .verify();
    }

    @Test
    void givenValidBookId_whenUpdateShouldUpdate() {
        List<R2dbcTag> tags = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            R2dbcTag tag = new R2dbcTag();
            tag.setKey("key" + i);
            tag.setValue("value" + i);
            tags.add(tag);
        }

        insertTags(tags);

        R2dbcTag tag1 = tags.get(2);
        tag1.setValue("value33");

        R2dbcTag tag2 = tags.get(5);
        tag2.setValue("value66");

        R2dbcTag tag3 = tags.get(8);
        tag3.setValue("value99");

        Set<R2dbcTag> expectedTags = new HashSet<>(tags);

        Set<R2dbcTag> tagsToUpdate = new LinkedHashSet<>();
        tagsToUpdate.add(tag1);
        tagsToUpdate.add(tag2);
        tagsToUpdate.add(tag3);

        tagDao.updateTags(tagsToUpdate)
                .thenMany(entityTemplate
                        .select(R2dbcTag.class)
                        .from("tags")
                        .all())
                .collect(Collectors.toSet())
                .as(StepVerifier::create)
                .consumeNextWith(actualTags -> {
                    log.info("Expected:: {}", expectedTags);
                    log.info("Actual:: {}", actualTags);

                    assertThat(actualTags).isEqualTo(expectedTags);
                })
                .verifyComplete();

    }

    private void insertTags(List<R2dbcTag> tags) {
        BookDaoTestUtils
                .insertDummyBook(entityTemplate.getDatabaseClient())
                .doOnSuccess(book -> tags.forEach(tag -> tag.setBookId(book.getUuid())))
                .thenMany(Flux.fromIterable(tags))
                .flatMap(author -> entityTemplate
                        .getDatabaseClient()
                        .sql(R2dbcPostgresqlTagDao.INSERT_STATEMENT)
                        .bind("$1", author.getBookUuid())
                        .bind("$2", author.getKey())
                        .bind("$3", author.getValue())
                        .map(row -> row.get("id", UUID.class))
                        .all())
                .collectList()
                .doOnSuccess(authorIds -> {
                    Iterator<UUID> idIterator = authorIds.iterator();
                    Iterator<R2dbcTag> authorIterator = tags.iterator();

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
        List<R2dbcTag> tags = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            R2dbcTag tag = new R2dbcTag();
            tag.setKey("key" + i);
            tag.setValue("value" + i);
            tags.add(tag);
        }

        insertTags(tags);
        String bookId = tags.get(0).getBookId();

        tagDao
                .deleteTagsByBookId(bookId)
                .then(entityTemplate
                        .getDatabaseClient()
                        .sql("SELECT * FROM tags")
                        .fetch()
                        .all()
                        .count())
                .as(StepVerifier::create)
                .expectNext(0L)
                .verifyComplete();
    }

    @Test
    void givenNonExistingBookId_whenDelete_shouldEmitEmpty() {
        tagDao.deleteTagsByBookId(UUID.randomUUID().toString())
                .as(StepVerifier::create)
                .expectSubscription()
                .expectNextCount(0L)
                .verifyComplete();
    }

    @Test
    void givenMalformedUuid_whenDelete_shouldEmitEmpty() {
        tagDao.deleteTagsByBookId("malformed-uuid")
                .as(StepVerifier::create)
                .expectSubscription()
                .expectNextCount(0L)
                .verifyComplete();
    }
}