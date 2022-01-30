package com.github.sujankumarmitra.libraryservice.v1.dao.impl;

import com.github.sujankumarmitra.libraryservice.v1.dao.impl.entity.R2dbcBook;
import com.github.sujankumarmitra.libraryservice.v1.dao.impl.entity.R2dbcPackage;
import com.github.sujankumarmitra.libraryservice.v1.dao.impl.entity.R2dbcPackageItem;
import com.github.sujankumarmitra.libraryservice.v1.exception.BookNotFoundException;
import com.github.sujankumarmitra.libraryservice.v1.exception.PackageNotFoundException;
import com.github.sujankumarmitra.libraryservice.v1.model.PackageItem;
import com.github.sujankumarmitra.libraryservice.v1.util.BookDaoTestUtils;
import com.github.sujankumarmitra.libraryservice.v1.util.PackageDaoTestUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.util.function.Tuples;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

import static com.github.sujankumarmitra.libraryservice.v1.util.DaoTestUtils.truncateAllTables;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author skmitra
 * @since Nov 28/11/21, 2021
 */
@Slf4j
class R2dbcPostgresqlPackageItemDaoTest extends AbstractDataR2dbcPostgresqlContainerDependentTest {

    private R2dbcPostgresqlPackageItemDao packageItemDao;
    @SuppressWarnings("FieldMayBeFinal")
    @Autowired
    private R2dbcEntityTemplate entityTemplate = null;

    @BeforeEach
    void setUp() {
        packageItemDao = new R2dbcPostgresqlPackageItemDao(entityTemplate.getDatabaseClient());
    }

    @AfterEach
    void tearDown() {
        truncateAllTables(entityTemplate.getDatabaseClient())
                .block();
    }


    @Test
    void givenValidPackageItem_whenCreate_shouldCreate() {
        R2dbcPackageItem expectedItem = new R2dbcPackageItem();

        BookDaoTestUtils
                .insertDummyBook(entityTemplate.getDatabaseClient())
                .doOnSuccess(book -> expectedItem.setBookId(book.getUuid()))
                .thenReturn(entityTemplate.getDatabaseClient())
                .flatMap(PackageDaoTestUtils::insertDummyPackage)
                .doOnSuccess(aPackage -> expectedItem.setPackageId(aPackage.getUuid()))
                .thenReturn(expectedItem)
                .map(Collections::singleton)
                .flatMapMany(packageItemDao::createItems)
                .next()
                .doOnSuccess(id -> expectedItem.setId(UUID.fromString(id)))
                .then(entityTemplate
                        .select(R2dbcPackageItem.class)
                        .from("package_items")
                        .one())
                .as(StepVerifier::create)
                .expectSubscription()
                .consumeNextWith(actualItem -> {
                    log.info("Expected {}", expectedItem);
                    log.info("Actual {}", actualItem);

                    assertThat(actualItem).isEqualTo(expectedItem);
                })
                .verifyComplete();
    }


    @Test
    void givenNonExistingPackage_whenCreate_shouldEmitPackageNotFoundException() {
        R2dbcPackageItem expectedItem = new R2dbcPackageItem();
        expectedItem.setPackageId(UUID.randomUUID());

        BookDaoTestUtils
                .insertDummyBook(entityTemplate.getDatabaseClient())
                .doOnSuccess(book -> expectedItem.setBookId(book.getUuid()))
                .thenReturn(expectedItem)
                .map(Collections::singleton)
                .flatMapMany(packageItemDao::createItems)
                .as(StepVerifier::create)
                .expectSubscription()
                .expectError(PackageNotFoundException.class)
                .verify();
    }

    @Test
    void givenMalformedPackageId_whenCreate_shouldEmitPackageNotFoundException() {
        Collection<PackageItem> packageItems = Collections.singleton(new PackageItem() {
            @Override
            public String getId() {
                return null;
            }

            @Override
            public String getPackageId() {
                return "malformed";
            }

            @Override
            public String getBookId() {
                return UUID.randomUUID().toString();
            }
        });

        Mono.just(packageItems)
                .flatMapMany(packageItemDao::createItems)
                .as(StepVerifier::create)
                .expectSubscription()
                .expectError(PackageNotFoundException.class)
                .verify();
    }

    @Test
    void givenMalformedBookId_whenCreate_shouldEmitBookNotFoundException() {
        Collection<PackageItem> packageItems = Collections.singleton(new PackageItem() {
            @Override
            public String getId() {
                return null;
            }

            @Override
            public String getPackageId() {
                return UUID.randomUUID().toString();
            }

            @Override
            public String getBookId() {
                return "malformed_uuid";
            }
        });

        Mono.just(packageItems)
                .flatMapMany(packageItemDao::createItems)
                .as(StepVerifier::create)
                .expectSubscription()
                .expectError(BookNotFoundException.class)
                .verify();
    }

    @Test
    void givenNonExistingBook_whenCreate_shouldEmitBookNotFoundException() {
        R2dbcPackageItem expectedItem = new R2dbcPackageItem();
        expectedItem.setBookId(UUID.randomUUID());

        PackageDaoTestUtils
                .insertDummyPackage(entityTemplate.getDatabaseClient())
                .doOnSuccess(aPackage -> expectedItem.setPackageId(aPackage.getUuid()))
                .thenReturn(expectedItem)
                .map(Collections::singleton)
                .flatMapMany(packageItemDao::createItems)
                .as(StepVerifier::create)
                .expectSubscription()
                .expectError(BookNotFoundException.class)
                .verify();
    }

    @Test
    void givenValidPackageId_whenSelect_shouldSelect() {

        Mono<UUID> bookIdMono = BookDaoTestUtils
                .insertDummyBook(entityTemplate.getDatabaseClient())
                .map(R2dbcBook::getUuid);

        Mono<UUID> packageIdMono = PackageDaoTestUtils
                .insertDummyPackage(entityTemplate.getDatabaseClient())
                .map(R2dbcPackage::getUuid);

        Mono.zip(packageIdMono, bookIdMono, Tuples::of)
                .flatMap(tuple2 ->
                        entityTemplate.getDatabaseClient()
                                .sql(R2dbcPostgresqlPackageItemDao.INSERT_STATEMENT)
                                .bind("$1", tuple2.getT1())
                                .bind("$2", tuple2.getT2())
                                .map(row -> row.get("id", UUID.class))
                                .one()
                                .thenReturn(tuple2.getT1()))
                .map(Object::toString)
                .flatMapMany(packageItemDao::getItemsByPackageId)
                .doOnNext(item -> log.info("fetched item {}", item))
                .as(StepVerifier::create)
                .expectSubscription()
                .expectNextCount(1L)
                .verifyComplete();


    }

    @Test
    void givenNonExistingPackageId_whenSelect_shouldEmitEmptyFlux() {
        packageItemDao
                .getItemsByPackageId(UUID.randomUUID().toString())
                .as(StepVerifier::create)
                .expectSubscription()
                .expectNextCount(0L)
                .verifyComplete();
    }

    @Test
    void givenMalformedPackageUuid_whenSelect_shouldEmitEmptyFlux() {
        packageItemDao
                .getItemsByPackageId("malformed")
                .as(StepVerifier::create)
                .expectSubscription()
                .expectNextCount(0L)
                .verifyComplete();
    }

    @Test
    void givenValidPackageId_whenDelete_shouldDelete() {
        Mono<UUID> bookIdMono = BookDaoTestUtils
                .insertDummyBook(entityTemplate.getDatabaseClient())
                .map(R2dbcBook::getUuid);

        Mono<UUID> packageIdMono = PackageDaoTestUtils
                .insertDummyPackage(entityTemplate.getDatabaseClient())
                .map(R2dbcPackage::getUuid);

        Mono.zip(packageIdMono, bookIdMono, Tuples::of)
                .flatMap(tuple2 ->
                        entityTemplate.getDatabaseClient()
                                .sql(R2dbcPostgresqlPackageItemDao.INSERT_STATEMENT)
                                .bind("$1", tuple2.getT1())
                                .bind("$2", tuple2.getT2())
                                .map(row -> row.get("id", UUID.class))
                                .one()
                                .thenReturn(tuple2.getT1()))
                .map(Object::toString)
                .flatMapMany(packageItemDao::deleteItemsByPackageId)
                .thenMany(entityTemplate
                        .select(R2dbcPackageItem.class)
                        .from("package_items")
                        .all())
                .as(StepVerifier::create)
                .expectSubscription()
                .expectNextCount(0L)
                .verifyComplete();
    }

    @Test
    void givenNonExistingPackageId_whenDelete_shouldEmitEmpty() {
        packageItemDao
                .deleteItemsByPackageId(UUID.randomUUID().toString())
                .as(StepVerifier::create)
                .expectSubscription()
                .verifyComplete();
    }

    @Test
    void givenMalformedPackageUuid_whenDelete_shouldEmitEmpty() {
        packageItemDao
                .deleteItemsByPackageId("malformed")
                .as(StepVerifier::create)
                .expectSubscription()
                .verifyComplete();
    }

}