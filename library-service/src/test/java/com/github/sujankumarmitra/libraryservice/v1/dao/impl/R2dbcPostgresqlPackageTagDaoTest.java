package com.github.sujankumarmitra.libraryservice.v1.dao.impl;

import com.github.sujankumarmitra.libraryservice.v1.dao.impl.entity.R2dbcPackageTag;
import com.github.sujankumarmitra.libraryservice.v1.exception.DuplicateTagKeyException;
import com.github.sujankumarmitra.libraryservice.v1.exception.PackageNotFoundException;
import com.github.sujankumarmitra.libraryservice.v1.model.PackageTag;
import com.github.sujankumarmitra.libraryservice.v1.util.PackageDaoTestUtils;
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
class R2dbcPostgresqlPackageTagDaoTest extends AbstractDataR2dbcPostgresqlContainerDependentTest {

    private R2dbcPostgresqlPackageTagDao tagDao = null;
    @Autowired
    private R2dbcEntityTemplate entityTemplate = null;

    @BeforeEach
    void setUp() {
        tagDao = new R2dbcPostgresqlPackageTagDao(entityTemplate.getDatabaseClient());
    }

    @AfterEach
    void tearDown() {
        truncateAllTables(entityTemplate.getDatabaseClient())
                .block();
    }

    @Test
    void givenValidPackageId_whenInsert_ShouldInsert() {

        R2dbcPackageTag tag1 = new R2dbcPackageTag();
        tag1.setKey("key1");
        tag1.setValue("value1");

        R2dbcPackageTag tag2 = new R2dbcPackageTag();
        tag2.setKey("key2");
        tag2.setValue("value2");

        R2dbcPackageTag tag3 = new R2dbcPackageTag();
        tag3.setKey("key3");
        tag3.setValue("value3");

        Collection<R2dbcPackageTag> tags = List.of(tag1, tag2, tag3);


        PackageDaoTestUtils.insertDummyPackage(entityTemplate.getDatabaseClient())
                .doOnSuccess(aPackage -> tags.forEach(tag -> tag.setPackageId(aPackage.getUuid())))
                .thenReturn(tags)
                .flatMapMany(tagDao::createTags)
                .then(entityTemplate
                        .select(R2dbcPackageTag.class)
                        .from("package_tags")
                        .all()
                        .count())
                .as(StepVerifier::create)
                .expectNext(3L)
                .verifyComplete();
    }

    @Test
    void givenDuplicateTagKeys_whenInsert_ShouldEmitError() {

        R2dbcPackageTag tag1 = new R2dbcPackageTag();
        tag1.setKey("key1");
        tag1.setValue("value1");

        R2dbcPackageTag tag2 = new R2dbcPackageTag();
        tag2.setKey("key1");
        tag2.setValue("value2");

        Collection<R2dbcPackageTag> tags = List.of(tag1, tag2);


        PackageDaoTestUtils.insertDummyPackage(entityTemplate.getDatabaseClient())
                .doOnSuccess(aPackage -> tags.forEach(tag -> tag.setPackageId(aPackage.getUuid())))
                .thenReturn(tags)
                .flatMapMany(tagDao::createTags)
                .then()
                .as(StepVerifier::create)
                .expectError(DuplicateTagKeyException.class)
                .verify();
    }

    @Test
    void givenNonExistingPackageId_whenSelect_shouldEmitEmpty() {
        tagDao.getTagsByPackageId(UUID.randomUUID().toString())
                .as(StepVerifier::create)
                .expectSubscription()
                .expectNextCount(0L)
                .verifyComplete();
    }

    @Test
    void givenMalformedPackageUuid_whenSelect_shouldEmitEmpty() {
        tagDao.getTagsByPackageId("malformed-uuid")
                .as(StepVerifier::create)
                .expectSubscription()
                .expectNextCount(0L)
                .verifyComplete();
    }

    @Test
    void givenMalformedPackageId_whenInsert_shouldEmitError() {

        Set<PackageTag> tags = Set.of(new PackageTag() {
            @Override
            public String getId() {
                return null;
            }

            @Override
            public String getPackageId() {
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
                .expectError(PackageNotFoundException.class)
                .verify();
    }

    @Test
    void givenNonExistingPackageId_whenInsert_shouldEmitError() {
        R2dbcPackageTag tag1 = new R2dbcPackageTag();
        tag1.setPackageId(UUID.randomUUID());
        tag1.setKey("key1");
        tag1.setValue("value1");

        R2dbcPackageTag tag2 = new R2dbcPackageTag();
        tag2.setPackageId(UUID.randomUUID());
        tag2.setKey("key2");
        tag2.setValue("value2");

        Set<R2dbcPackageTag> tags = Set.of(tag1, tag2);

        tagDao.createTags(tags)
                .as(StepVerifier::create)
                .expectSubscription()
                .expectErrorSatisfies(th -> {
                    assertThat(th).isExactlyInstanceOf(PackageNotFoundException.class);
                    log.info("Exception errors:: {}", ((PackageNotFoundException) th).getErrors());
                })
                .verify();
    }

    @Test
    void givenValidPackageId_whenUpdateShouldUpdate() {
        List<R2dbcPackageTag> tags = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            R2dbcPackageTag tag = new R2dbcPackageTag();
            tag.setKey("key" + i);
            tag.setValue("value" + i);
            tags.add(tag);
        }

        insertTags(tags);

        R2dbcPackageTag tag1 = tags.get(2);
        tag1.setValue("value33");

        R2dbcPackageTag tag2 = tags.get(5);
        tag2.setValue("value66");

        R2dbcPackageTag tag3 = tags.get(8);
        tag3.setValue("value99");

        Set<R2dbcPackageTag> expectedTags = new HashSet<>(tags);

        Set<R2dbcPackageTag> tagsToUpdate = new LinkedHashSet<>();
        tagsToUpdate.add(tag1);
        tagsToUpdate.add(tag2);
        tagsToUpdate.add(tag3);

        tagDao.updateTags(tagsToUpdate)
                .thenMany(entityTemplate
                        .select(R2dbcPackageTag.class)
                        .from("package_tags")
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

    private void insertTags(List<R2dbcPackageTag> tags) {
        PackageDaoTestUtils
                .insertDummyPackage(entityTemplate.getDatabaseClient())
                .doOnSuccess(book -> tags.forEach(tag -> tag.setPackageId(book.getUuid())))
                .thenMany(Flux.fromIterable(tags))
                .flatMapSequential(author -> entityTemplate
                        .getDatabaseClient()
                        .sql(R2dbcPostgresqlPackageTagDao.INSERT_STATEMENT)
                        .bind("$1", author.getPackageUuid())
                        .bind("$2", author.getKey())
                        .bind("$3", author.getValue())
                        .map(row -> row.get("id", UUID.class))
                        .all())
                .collectList()
                .doOnSuccess(authorIds -> {
                    Iterator<UUID> idIterator = authorIds.iterator();
                    Iterator<R2dbcPackageTag> authorIterator = tags.iterator();

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
    void givenValidPackageId_whenDelete_shouldDelete() {
        List<R2dbcPackageTag> tags = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            R2dbcPackageTag tag = new R2dbcPackageTag();
            tag.setKey("key" + i);
            tag.setValue("value" + i);
            tags.add(tag);
        }

        insertTags(tags);
        String packageId = tags.get(0).getPackageId();

        tagDao
                .deleteTagsByPackageId(packageId)
                .then(entityTemplate
                        .getDatabaseClient()
                        .sql("SELECT * FROM package_tags")
                        .fetch()
                        .all()
                        .count())
                .as(StepVerifier::create)
                .expectNext(0L)
                .verifyComplete();
    }

    @Test
    void givenNonExistingPackageId_whenDelete_shouldEmitEmpty() {
        tagDao.deleteTagsByPackageId(UUID.randomUUID().toString())
                .as(StepVerifier::create)
                .expectSubscription()
                .expectNextCount(0L)
                .verifyComplete();
    }

    @Test
    void givenMalformedUuid_whenDelete_shouldEmitEmpty() {
        tagDao.deleteTagsByPackageId("malformed-uuid")
                .as(StepVerifier::create)
                .expectSubscription()
                .expectNextCount(0L)
                .verifyComplete();
    }
}