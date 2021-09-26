package com.github.sujankumarmitra.assetservice.v1.dao;

import com.github.sujankumarmitra.assetservice.v1.model.Asset;
import com.github.sujankumarmitra.assetservice.v1.model.DefaultAsset;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import static java.util.Collections.emptySet;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

/**
 * @author skmitra
 * @since Sep 25/09/21, 2021
 */
@Repository
@AllArgsConstructor
@Slf4j
public class MongoAssetDao implements AssetDao {

    @NonNull
    private final ReactiveMongoTemplate mongoTemplate;

    @Override
    public Mono<Asset> insert(Asset asset) {
        MongoAssetDocument assetDocument = MongoAssetDocument.newBuilder()
                .id(asset.getId())
                .name(asset.getName())
                .permissions(emptySet())
                .build();
        return mongoTemplate
                .insert(assetDocument)
                .cast(Asset.class);
    }

    @Override
    public Mono<Void> remove(String assetId) {
        return mongoTemplate
                .remove(query(where("_id").is(assetId)), MongoAssetDocument.class)
                .then();
    }

    @Override
    public Mono<Asset> findOne(String assetId) {
        Query query = query(where("_id").is(assetId));
        query.fields().include("_id","name");

        return mongoTemplate
                .findOne(query, DefaultAsset.class, getCollectionName())
                .cast(Asset.class);
    }

    private String getCollectionName() {
        return mongoTemplate.getCollectionName(MongoAssetDocument.class);
    }

}
