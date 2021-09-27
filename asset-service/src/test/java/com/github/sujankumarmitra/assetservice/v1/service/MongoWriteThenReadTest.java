package com.github.sujankumarmitra.assetservice.v1.service;

import com.github.sujankumarmitra.assetservice.v1.controller.dto.CreateAssetRequest;
import com.github.sujankumarmitra.assetservice.v1.dao.MongoAssetDao;
import com.github.sujankumarmitra.assetservice.v1.model.Asset;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

/**
 * @author skmitra
 * @since Sep 27/09/21, 2021
 */
@DataMongoTest
class MongoWriteThenReadTest {

    private AssetService assetService;
    @Autowired
    private ReactiveMongoTemplate mongoTemplate;

    @BeforeEach
    void setUp() {
        assetService = new DefaultAssetService(new MongoAssetDao(mongoTemplate));
    }

    @Test
    void testWriteThenRead() {

        Mono<Asset> assetMono = assetService
                .createAsset(new CreateAssetRequest("name"))
                .map(Asset::getId)
                .flatMap(assetService::getAsset);

        StepVerifier.create(assetMono)
                .expectSubscription()
                .consumeNextWith(asset -> {
                    System.out.println(asset);
                })
                .verifyComplete();
    }
}
