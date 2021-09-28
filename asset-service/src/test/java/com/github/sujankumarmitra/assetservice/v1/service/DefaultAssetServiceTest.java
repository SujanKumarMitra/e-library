package com.github.sujankumarmitra.assetservice.v1.service;

import com.github.sujankumarmitra.assetservice.v1.dao.AssetDao;
import com.github.sujankumarmitra.assetservice.v1.model.Asset;
import com.github.sujankumarmitra.assetservice.v1.model.DefaultAsset;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;

/**
 * @author skmitra
 * @since Sep 26/09/21, 2021
 */
class DefaultAssetServiceTest {

    protected DefaultAssetService serviceUnderTest;
    private AssetDao assetDao;

    @BeforeEach
    void setUp() {
        assetDao = Mockito.mock(AssetDao.class);
        serviceUnderTest = new DefaultAssetService(assetDao);
    }

    @Test
    void createAsset() {

        Mockito.doAnswer(answer -> {
            Asset assetToSave = answer.getArgument(0);
            return Mono.just(new DefaultAsset(UUID.randomUUID().toString(), assetToSave.getName()));
        }).when(assetDao).insert(any());


        Mono<Asset> createdAsset = serviceUnderTest.createAsset(new AssetImpl(null, "some_name"));

        StepVerifier.create(createdAsset)
                .consumeNextWith(asset -> assertNotNull(asset.getId()))
                .verifyComplete();
    }

    @Test
    void deleteAsset() {
        Mockito.doReturn(Mono.empty())
                .when(assetDao).remove(any());

        Mono<Void> voidMono = serviceUnderTest.deleteAsset("someId");

        StepVerifier.create(voidMono)
                .expectComplete()
                .verify();

    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    @AllArgsConstructor
    private static class AssetImpl extends Asset {
        private String id;
        private String name;
    }
}