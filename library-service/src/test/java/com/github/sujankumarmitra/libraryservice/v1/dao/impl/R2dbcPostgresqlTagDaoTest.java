package com.github.sujankumarmitra.libraryservice.v1.dao.impl;

import com.github.sujankumarmitra.libraryservice.v1.dao.impl.entity.R2dbcAuthor;
import com.github.sujankumarmitra.libraryservice.v1.dao.impl.entity.R2dbcTag;
import com.github.sujankumarmitra.libraryservice.v1.exception.BookNotFoundException;
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
    void givenValidBookId_whenInsertShouldInsert() {
        Set<R2dbcTag> tags = new HashSet<>();

        R2dbcTag tag1 = new R2dbcTag();
        tag1.setKey("key1");
        tag1.setValue("value1");

        R2dbcTag tag2 = new R2dbcTag();
        tag2.setKey("key2");
        tag2.setValue("value2");

        R2dbcTag tag3 = new R2dbcTag();
        tag3.setKey("key3");
        tag3.setValue("value3");

        tags.add(tag1);
        tags.add(tag2);
        tags.add(tag3);


        entityTemplate.getDatabaseClient()
                .sql(R2dbcPostgresqlBookDao.INSERT_STATEMENT)
                .bind("$1", "title")
                .bind("$2", "publisher")
                .bind("$3", "edition")
                .bindNull("$4", String.class)
                .map(row -> row.get("id", UUID.class))
                .one()
                .doOnSuccess(bookId -> tags.forEach(tag -> tag.setBookId(bookId)))
                .thenReturn(tags)
                .flatMap(tagDao::insertTags)
                .then(
                        entityTemplate
                                .select(R2dbcTag.class)
                                .from("tags")
                                .all()
                                .collect(Collectors.toCollection(HashSet::new)))
                .as(StepVerifier::create)
                .consumeNextWith(actualTags -> {
                    log.info("Expected Tags {}", tags);
                    log.info("Actual Tags {}", actualTags);

                    assertThat(actualTags).isEqualTo(tags);
                })
                .verifyComplete();
    }

    @Test
    void givenNonExistingBookId_whenSelect_shouldEmitEmpty() {
        tagDao.selectTags(UUID.randomUUID().toString())
                .as(StepVerifier::create)
                .expectSubscription()
                .expectNextCount(0L)
                .verifyComplete();
    }

    @Test
    void givenMalformedUuid_whenSelect_shouldEmitEmpty() {
        tagDao.selectTags("malformed-uuid")
                .as(StepVerifier::create)
                .expectSubscription()
                .expectNextCount(0L)
                .verifyComplete();
    }


    @Test
    void givenMalformedUuidBookId_whenInsert_shouldEmitError() {

        Set<Tag> tags = Set.of(new Tag() {
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

        tagDao.insertTags(tags)
                .as(StepVerifier::create)
                .expectSubscription()
                .expectError(BookNotFoundException.class)
                .verify();
    }

    @Test
    void givenNonExistingBookId_whenInsert_shouldEmitError() {
        R2dbcTag tag1 = new R2dbcTag();
        tag1.setBookId(UUID.randomUUID());
        tag1.setKey("key");
        tag1.setValue("value");

        R2dbcTag tag2 = new R2dbcTag();
        tag2.setBookId(UUID.randomUUID());
        tag2.setKey("key");
        tag2.setValue("value");

        Set<R2dbcTag> tags = Set.of(tag1, tag2);

        tagDao.insertTags(tags)
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

        Set<R2dbcTag> tagsToUpdate = new LinkedHashSet<>();
        tagsToUpdate.add(tag1);
        tagsToUpdate.add(tag2);
        tagsToUpdate.add(tag3);

        Collections.sort(tags, Comparator.comparing(R2dbcTag::getKey));


        tagDao.updateTags(tagsToUpdate)
                .thenMany(entityTemplate
                        .select(R2dbcTag.class)
                        .from("tags")
                        .all())
                .collectSortedList(Comparator.comparing(R2dbcTag::getKey))
                .as(StepVerifier::create)
                .consumeNextWith(actualTags -> {
                    log.info("Expected:: {}", tags);
                    log.info("Actual:: {}", tags);

                    assertThat(actualTags).isEqualTo(tags);
                })
                .verifyComplete();

    }

    private void insertTags(List<R2dbcTag> tags) {
        entityTemplate
                .getDatabaseClient()
                .sql(R2dbcPostgresqlBookDao.INSERT_STATEMENT)
                .bind("$1", "title")
                .bind("$2", "publisher")
                .bind("$3", "edition")
                .bindNull("$4", String.class)
                .map(row -> row.get("id", UUID.class))
                .one()
                .doOnSuccess(bookId -> tags.forEach(tag -> tag.setBookId(bookId)))
                .thenMany(Flux.fromIterable(tags))
                .flatMap(tag -> entityTemplate
                        .getDatabaseClient()
                        .sql("INSERT INTO tags(book_id,key,value) VALUES ($1,$2,$3)")
                        .bind("$1", tag.getBookUuid())
                        .bind("$2", tag.getKey())
                        .bind("$3", tag.getValue())
                        .fetch()
                        .rowsUpdated())
                .as(StepVerifier::create)
                .expectNextCount(tags.size())
                .verifyComplete();
    }


}