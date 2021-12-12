package com.github.sujankumarmitra.assetservice.v1.dao;

import com.github.sujankumarmitra.assetservice.v1.controller.dto.CreateAssetRequest;
import com.github.sujankumarmitra.assetservice.v1.model.DefaultAsset;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Objects;
import java.util.UUID;

import static com.github.sujankumarmitra.assetservice.v1.model.AccessLevel.PRIVATE;
import static com.github.sujankumarmitra.assetservice.v1.model.AccessLevel.PUBLIC;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author skmitra
 * @since Nov 16/11/21, 2021
 */
@Slf4j
class R2dbcPostgresqlAssetDaoTest extends AbstractDataR2dbcPostgreSQLContainerDependentTest {

    private R2dbcPostgresqlAssetDao assetDao;
    @Autowired
    private R2dbcEntityTemplate entityTemplate = null;

    @BeforeEach
    void setUp() {
        assetDao = new R2dbcPostgresqlAssetDao(entityTemplate.getDatabaseClient());
    }

    @AfterEach
    void tearDown() {
        entityTemplate
                .getDatabaseClient()
                .sql("DELETE FROM assets")
                .fetch()
                .all()
                .blockLast();
    }


    @Test
    void givenValidAssetName_whenInsert_shouldInsertAsset() {
        CreateAssetRequest expectedAsset = new CreateAssetRequest(null, "assetName", "owner", PUBLIC);

        assetDao.insert(expectedAsset)
                .doOnNext(actualAsset -> expectedAsset.setId(actualAsset.getId()))
                .as(StepVerifier::create)
                .expectSubscription()
                .consumeNextWith(actual -> {
                    log.info("Expected {}", expectedAsset);
                    log.info("Actual {}", actual);

                    assertThat(actual).isEqualTo(expectedAsset);
                })
                .verifyComplete();

    }


    @Test
    void givenValidId_whenFetched_shouldFetch() {

        DefaultAsset expectedAsset = DefaultAsset
                .builder()
                .id("")
                .name("name")
                .ownerId("owner")
                .accessLevel(PRIVATE)
                .build();

        entityTemplate
                .getDatabaseClient()
                .sql(R2dbcPostgresqlAssetDao.INSERT_STATEMENT)
                .bind("$1", expectedAsset.getName())
                .bind("$2", expectedAsset.getOwnerId())
                .bind("$3", expectedAsset.getAccessLevel().toString())
                .map(row -> row.get("id", UUID.class))
                .one()
                .map(Objects::toString)
                .doOnNext(expectedAsset::setId)
                .flatMap(assetDao::findOne)
                .as(StepVerifier::create)
                .expectSubscription()
                .consumeNextWith(actual -> {
                    log.info("Expected {}", expectedAsset);
                    log.info("Actual {}", actual);

                    assertThat(actual).isEqualTo(expectedAsset);

                })
                .verifyComplete();
    }

    @Test
    void givenInvalidId_whenFetched_shouldNotFetchAnything() {

        assetDao.findOne("INVALID-ID")
                .as(StepVerifier::create)
                .expectSubscription()
                .verifyComplete();
    }


    @Test
    void givenInvalidAssetId_whenDeleted_shouldEmitComplete() {
        assetDao.delete(UUID.randomUUID().toString())
                .as(StepVerifier::create)
                .expectSubscription()
                .verifyComplete();
    }


    @Test
    void givenValidAssetId_whenDeleted_shouldEmitComplete() {
        DefaultAsset expectedAsset = DefaultAsset
                .builder()
                .id("")
                .name("name")
                .ownerId("owner")
                .accessLevel(PRIVATE)
                .build();

        entityTemplate
                .getDatabaseClient()
                .sql(R2dbcPostgresqlAssetDao.INSERT_STATEMENT)
                .bind("$1", expectedAsset.getName())
                .bind("$2", expectedAsset.getOwnerId())
                .bind("$3", expectedAsset.getAccessLevel().toString())
                .map(row -> row.get("id", UUID.class))
                .one()
                .map(Objects::toString)
                .doOnNext(expectedAsset::setId)
                .then(Mono.defer(() -> assetDao.delete(expectedAsset.getId())))
                .then(Mono.defer(() -> entityTemplate.getDatabaseClient()
                        .sql("SELECT * from assets WHERE id=$1")
                        .bind("$1", UUID.fromString(expectedAsset.getId()))
                        .fetch()
                        .all()
                        .count()))
                .as(StepVerifier::create)
                .expectSubscription()
                .expectNext(0L)
                .verifyComplete();

    }

}