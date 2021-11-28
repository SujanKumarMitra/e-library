package com.github.sujankumarmitra.assetservice.v1.dao;

import com.github.javafaker.Faker;
import com.github.sujankumarmitra.assetservice.v1.controller.dto.CreateAssetRequest;
import com.github.sujankumarmitra.assetservice.v1.model.Asset;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

/**
 * @author skmitra
 * @since Nov 16/11/21, 2021
 */
@Slf4j
class R2dbcPostgresqlAssetDaoTest extends AbstractDataR2dbcPostgreSQLContainerDependentTest{

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
        String assetName = new Faker().name().name();
        Asset asset = new CreateAssetRequest(assetName);

        assetDao.insert(asset)
                .as(StepVerifier::create)
                .expectSubscription()
                .consumeNextWith(savedAsset -> {
                    log.info("{}", savedAsset);
                    log.info("Saved Asset Id" + savedAsset.getId());
                })
                .verifyComplete();

    }


    @Test
    void givenValidId_whenFetched_shouldFetch() {

        UUID id = UUID.randomUUID();
        String name = new Faker().name().name();

        entityTemplate
                .getDatabaseClient()
                .sql("INSERT INTO assets VALUES ($1,$2)")
                .bind("$1", id)
                .bind("$2", name)
                .fetch()
                .all()
                .then(assetDao.findOne(id.toString()))
                .as(StepVerifier::create)
                .expectSubscription()
                .expectNextMatches(asset -> {
                    log.info("{}", asset);
                    return asset.getId().equals(id.toString()) &&
                            asset.getName().equals(name);
                })
                .verifyComplete();
    }

    @Test
    void givenInvalidId_whenFetched_shouldNotFetchAnything() {

        assetDao.findOne("INVALID-ID")
                .as(StepVerifier::create)
                .expectSubscription()
                .expectNextCount(0)
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

        UUID id = UUID.randomUUID();
        String name = new Faker().name().name();

        entityTemplate
                .getDatabaseClient()
                .sql("INSERT INTO assets VALUES ($1,$2)")
                .bind("$1", id)
                .bind("$2", name)
                .fetch()
                .rowsUpdated()
                .flatMap(updateCount ->
                        updateCount == 1 ?
                                Mono.empty() :
                                Mono.error(new RuntimeException("updateCount " + updateCount)))
                .then(assetDao.delete(id.toString()))
                .then(entityTemplate.getDatabaseClient()
                        .sql("SELECT * from assets WHERE id=$1")
                        .bind("$1", id)
                        .fetch()
                        .all()
                        .count())
                .as(StepVerifier::create)
                .expectSubscription()
                .expectNext(0L)
                .verifyComplete();

    }

}