package com.github.sujankumarmitra.assetservice.v1.service;

import com.github.javafaker.Faker;
import com.github.sujankumarmitra.assetservice.v1.model.Asset;
import com.github.sujankumarmitra.assetservice.v1.model.DefaultAsset;
import com.github.sujankumarmitra.assetservice.v1.model.MongoDocumentAsset;
import com.mongodb.reactivestreams.client.MongoCollection;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

/**
 * @author skmitra
 * @since Sep 25/09/21, 2021
 */
@DataMongoTest(properties = "logging.level.org.mongodb.*=DEBUG")
@Slf4j
class MongoBasedAssetServiceTest {

    @Autowired
    private ReactiveMongoTemplate mongoTemplate = null;
    protected MongoBasedAssetService serviceUnderTest;

    private Faker faker;

    @BeforeEach
    void setUp() {
        serviceUnderTest = new MongoBasedAssetService(mongoTemplate);
        faker = new Faker();
    }

    @AfterEach
    void tearDown() {
        mongoTemplate.remove(MongoDocumentAsset.class).all().block();
    }

    @Test
    void testServiceAutowiredSuccessfully() {
        assertThat(serviceUnderTest).isNotNull();
    }

    @Test
    void givenValidAsset_whenCreated_shouldSaveAndReturnAssetWithId() {
        DefaultAsset assetToCreate = new DefaultAsset("VALID", "assetName");
        Mono<Asset> createdAsset = serviceUnderTest.createAsset(assetToCreate);

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
        return mongoTemplate.getCollectionName(MongoDocumentAsset.class);
    }

    @Test
    void givenValidAsset_whenDeleted_shouldDelete() {

        String assetId = mongoTemplate
                .insert(new MongoDocumentAsset(null, faker.file().fileName()))
                .block()
                .getId();

        serviceUnderTest.deleteAsset(assetId).block();

        Asset nullAsset = findOneById(assetId).block();
        assertThat(nullAsset).isNull();

    }

    @Test
    void givenValidAssetId_whenFetched_shouldFetch() {

        Asset insertedAsset = mongoTemplate
                .insert(new MongoDocumentAsset(null, faker.file().fileName()))
                .block();

        Document insertedDocument = mongoTemplate.getCollection(getCollectionName())
                .flatMapMany(MongoCollection::find)
                .next()
                .block();

        System.out.println(insertedDocument);

        Asset fetchedAsset = serviceUnderTest.getAsset(insertedAsset.getId()).block();
        Assertions.assertEquals(insertedAsset, fetchedAsset);
    }

    private Mono<MongoDocumentAsset> findOneById(String assetId) {
        return mongoTemplate.findOne(query(where("_id").is(assetId))
                , MongoDocumentAsset.class);
    }

}