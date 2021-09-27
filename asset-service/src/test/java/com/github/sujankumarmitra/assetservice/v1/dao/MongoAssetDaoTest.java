package com.github.sujankumarmitra.assetservice.v1.dao;

import com.github.javafaker.Faker;
import com.github.sujankumarmitra.assetservice.v1.model.Asset;
import com.github.sujankumarmitra.assetservice.v1.model.DefaultAsset;
import com.mongodb.reactivestreams.client.MongoCollection;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static java.util.Collections.emptySet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

/**
 * @author skmitra
 * @since Sep 25/09/21, 2021
 */
@DataMongoTest(properties = "logging.level.org.mongodb.*=DEBUG")
@Slf4j
class MongoAssetDaoTest {

    public static final String INVALID_ID = "INVALID_ID";
    @Autowired
    private ReactiveMongoTemplate mongoTemplate = null;
    protected MongoAssetDao daoUnderTest;

    private Faker faker;

    @BeforeEach
    void setUp() {
        daoUnderTest = new MongoAssetDao(mongoTemplate);
        faker = new Faker();
    }

    @AfterEach
    void tearDown() {
        mongoTemplate.remove(MongoAssetDocument.class).all().block();
    }

    @Test
    void testServiceAutowiredSuccessfully() {
        assertThat(daoUnderTest).isNotNull();
    }

    @Test
    void givenValidAsset_whenCreated_shouldSaveAndReturnAssetWithId() {
        DefaultAsset assetToCreate = new DefaultAsset(ObjectId.get().toHexString(), "assetName");
        Mono<Asset> createdAsset = daoUnderTest.insert(assetToCreate);

        StepVerifier.create(createdAsset)
                .expectSubscription()
                .assertNext(asset -> {
                    String assetId = asset.getId();
                    assertThat(assetId).isNotNull();

                    System.out.println(asset);

                }).verifyComplete();

        Flux<Document> insertedDocs = mongoTemplate.getCollection(getCollectionName())
                .flatMapMany(MongoCollection::find);


        StepVerifier.create(insertedDocs)
                .expectSubscription()
                .expectNextCount(1)
                .verifyComplete();

    }

    private String getCollectionName() {
        return mongoTemplate.getCollectionName(MongoAssetDocument.class);
    }

    @Test
    void givenAssetId_whenDeleted_shouldDelete() {

        String assetId = mongoTemplate
                .insert(new MongoAssetDocument(null, faker.file().fileName(), emptySet()))
                .block()
                .getId();

        daoUnderTest.remove(assetId).block();

        Asset nullAsset = findOneById(assetId).block();
        assertThat(nullAsset).isNull();

    }

    @Test
    void givenValidAssetId_whenFetched_shouldFetch() {

        Asset insertedAsset = mongoTemplate
                .insert(new MongoAssetDocument(null, faker.file().fileName(), emptySet()))
                .block();

        Document insertedDocument = mongoTemplate.getCollection(getCollectionName())
                .flatMapMany(MongoCollection::find)
                .next()
                .block();

        System.out.println(insertedDocument);

        Asset fetchedAsset = daoUnderTest.findOne(insertedAsset.getId()).block();
        Assertions.assertEquals(insertedAsset, fetchedAsset);
    }

    @Test
    void givenInvalidAssetId_whenFetched_shouldNotEmitAnything() {
        Mono<Asset> asset = daoUnderTest.findOne(INVALID_ID);

        StepVerifier.create(asset)
                .expectComplete()
                .verify();
    }

    private Mono<MongoAssetDocument> findOneById(String assetId) {
        return mongoTemplate.findOne(query(where("_id").is(assetId))
                , MongoAssetDocument.class);
    }

}