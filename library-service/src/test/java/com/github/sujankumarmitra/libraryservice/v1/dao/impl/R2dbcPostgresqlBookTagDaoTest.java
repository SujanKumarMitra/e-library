package com.github.sujankumarmitra.libraryservice.v1.dao.impl;

import com.github.sujankumarmitra.libraryservice.v1.dao.impl.entity.R2dbcBookTag;
import com.github.sujankumarmitra.libraryservice.v1.exception.BookNotFoundException;
import com.github.sujankumarmitra.libraryservice.v1.exception.DuplicateTagKeyException;
import com.github.sujankumarmitra.libraryservice.v1.model.BookTag;
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
import java.util.stream.Collectors;

import static com.github.sujankumarmitra.libraryservice.v1.util.DaoTestUtils.truncateAllTables;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author skmitra
 * @since Nov 23/11/21, 2021
 */
@Slf4j
class R2dbcPostgresqlBookTagDaoTest extends AbstractDataR2dbcPostgresqlContainerDependentTest {

    private R2dbcPostgresqlBookTagDao tagDao = null;
    @Autowired
    private R2dbcEntityTemplate entityTemplate = null;

    @BeforeEach
    void setUp() {
        tagDao = new R2dbcPostgresqlBookTagDao(entityTemplate.getDatabaseClient());
    }

    @AfterEach
    void tearDown() {
        truncateAllTables(entityTemplate.getDatabaseClient())
                .block();
    }

    @Test
    void givenValidBookId_whenInsert_ShouldInsert() {

        R2dbcBookTag tag1 = new R2dbcBookTag();
        tag1.setKey("key1");
        tag1.setValue("value1");

        R2dbcBookTag tag2 = new R2dbcBookTag();
        tag2.setKey("key2");
        tag2.setValue("value2");

        R2dbcBookTag tag3 = new R2dbcBookTag();
        tag3.setKey("key3");
        tag3.setValue("value3");

        Collection<R2dbcBookTag> tags = List.of(tag1, tag2, tag3);


        BookDaoTestUtils.insertDummyBook(entityTemplate.getDatabaseClient())
                .doOnSuccess(book -> tags.forEach(tag -> tag.setBookId(book.getUuid())))
                .thenReturn(tags)
                .flatMapMany(tagDao::createTags)
                .then(entityTemplate
                        .select(R2dbcBookTag.class)
                        .from("book_tags")
                        .all()
                        .count())
                .as(StepVerifier::create)
                .expectNext(3L)
                .verifyComplete();
    }

    @Test
    void givenDuplicateTagKeys_whenInsert_ShouldEmitError() {

        R2dbcBookTag tag1 = new R2dbcBookTag();
        tag1.setKey("key1");
        tag1.setValue("value1");

        R2dbcBookTag tag2 = new R2dbcBookTag();
        tag2.setKey("key1");
        tag2.setValue("value2");

        Collection<R2dbcBookTag> tags = List.of(tag1, tag2);


        BookDaoTestUtils.insertDummyBook(entityTemplate.getDatabaseClient())
                .doOnSuccess(book -> tags.forEach(tag -> tag.setBookId(book.getUuid())))
                .thenReturn(tags)
                .flatMapMany(tagDao::createTags)
                .then()
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

        Set<BookTag> tags = Set.of(new BookTag() {
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
        R2dbcBookTag tag1 = new R2dbcBookTag();
        tag1.setBookId(UUID.randomUUID());
        tag1.setKey("key1");
        tag1.setValue("value1");

        R2dbcBookTag tag2 = new R2dbcBookTag();
        tag2.setBookId(UUID.randomUUID());
        tag2.setKey("key2");
        tag2.setValue("value2");

        Set<R2dbcBookTag> tags = Set.of(tag1, tag2);

        tagDao.createTags(tags)
                .as(StepVerifier::create)
                .expectSubscription()
                .expectErrorSatisfies(th -> {
                    assertThat(th).isExactlyInstanceOf(BookNotFoundException.class);
                    log.info("Exception errors:: {}", ((BookNotFoundException) th).getErrors());
                })
                .verify();
    }


    private void insertTags(List<R2dbcBookTag> tags) {
        BookDaoTestUtils
                .insertDummyBook(entityTemplate.getDatabaseClient())
                .doOnSuccess(book -> tags.forEach(tag -> tag.setBookId(book.getUuid())))
                .thenMany(Flux.fromIterable(tags))
                .flatMapSequential(author -> entityTemplate
                        .getDatabaseClient()
                        .sql(R2dbcPostgresqlBookTagDao.INSERT_STATEMENT)
                        .bind("$1", author.getBookUuid())
                        .bind("$2", author.getKey())
                        .bind("$3", author.getValue())
                        .map(row -> row.get("id", UUID.class))
                        .all())
                .collectList()
                .doOnSuccess(authorIds -> {
                    Iterator<UUID> idIterator = authorIds.iterator();
                    Iterator<R2dbcBookTag> authorIterator = tags.iterator();

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
        List<R2dbcBookTag> tags = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            R2dbcBookTag tag = new R2dbcBookTag();
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
                        .sql("SELECT * FROM book_tags")
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