package com.github.sujankumarmitra.assetservice.v1.service;

import com.github.sujankumarmitra.assetservice.v1.model.Asset;
import com.github.sujankumarmitra.assetservice.v1.model.MongoDocumentAsset;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

/**
 * @author skmitra
 * @since Sep 25/09/21, 2021
 */
@Service
@AllArgsConstructor
@Slf4j
public class MongoBasedAssetService implements AssetService {

    @NonNull
    private final ReactiveMongoTemplate mongoTemplate;

    @Override
    public Mono<Asset> createAsset(Asset asset) {
        MongoDocumentAsset mongoAsset = new MongoDocumentAsset(null, asset.getName());
        return mongoTemplate
                .insert(mongoAsset);
    }

    @Override
    public Mono<Void> deleteAsset(String assetId) {
        return mongoTemplate
                .remove(query(where("_id").is(assetId)), MongoDocumentAsset.class)
                .then();
    }

    @Override
    public Mono<Asset> getAsset(String assetId) {
        return mongoTemplate
                .findOne(query(where("_id").is(assetId)), MongoDocumentAsset.class)
                .cast(Asset.class);
    }
}
