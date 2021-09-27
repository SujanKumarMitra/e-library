package com.github.sujankumarmitra.assetservice.v1.dao;

import com.github.sujankumarmitra.assetservice.v1.exception.AssetNotFoundException;
import com.github.sujankumarmitra.assetservice.v1.model.AssetPermission;
import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.client.model.UpdateOneModel;
import com.mongodb.client.model.WriteModel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SynchronousSink;

import java.util.List;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.pull;
import static com.mongodb.client.model.Updates.push;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;
import static org.springframework.data.mongodb.core.query.Criteria.where;

/**
 * @author skmitra
 * @since Sep 25/09/21, 2021
 */
@Repository
@AllArgsConstructor
public class MongoAssetPermissionDao implements AssetPermissionDao {

    @NonNull
    private final ReactiveMongoTemplate mongoTemplate;

    @Override
    public Mono<Void> upsert(AssetPermission permission) {
        List<WriteModel<Document>> bulkOperations = prepareBulkWrites(permission);
        String assetId = permission.getAssetId();

        return mongoTemplate
                .getCollection(mongoTemplate.getCollectionName(MongoAssetDocument.class))
                .flatMapMany(collection -> collection.bulkWrite(bulkOperations))
                .next()
                .handle((bulkWriteResult, sink) -> emitErrorIfNotUpdated(assetId, bulkWriteResult, sink));

    }

    private List<WriteModel<Document>> prepareBulkWrites(AssetPermission permission) {
        String subjectId = permission.getSubjectId();

        Bson idFilter = eq("_id", new ObjectId(permission.getAssetId()));
        Bson subjectFilter = eq("subjectId", subjectId);
        Bson pullDocument = pull("permissions", subjectFilter);
        UpdateOneModel<Document> pullOperation = new UpdateOneModel<>(idFilter, pullDocument);

        Document pushDocument = new Document()
                .append("subjectId", subjectId)
                .append("grantStartEpochMilliseconds", permission.getGrantStartEpochMilliseconds())
                .append("grantDurationInMilliseconds", permission.getGrantDurationInMilliseconds());

        Bson pushPermission = push("permissions", pushDocument);
        UpdateOneModel<Document> pushOperation = new UpdateOneModel<>(idFilter, pushPermission);

        return List.of(pullOperation, pushOperation);
    }

    private void emitErrorIfNotUpdated(String assetId, BulkWriteResult bulkWriteResult, SynchronousSink<Void> sink) {
        if (bulkWriteResult.getModifiedCount() == 0)
            sink.error(new AssetNotFoundException(assetId));
        else
            sink.complete();
    }

    @Override
    public Mono<AssetPermission> findOne(String assetId, String subjectId) {
        Aggregation aggregationPipeline = buildPipeline(assetId, subjectId);

        return mongoTemplate
                .aggregate(aggregationPipeline, MongoAssetDocument.class, MongoAssetPermissionDocument.class)
                .next()
                .doOnNext(perm -> perm.setAssetId(assetId))
                .cast(AssetPermission.class);

    }

    private Aggregation buildPipeline(String assetId, String subjectId) {
        AggregationOperation filterByAssetIdStage = match(where("_id").is(assetId));
        AggregationOperation unwindPermissionsArrayStage = unwind("$permissions");
        AggregationOperation filterBySubjectIdStage = match(where("permissions.subjectId").is(subjectId));
        AggregationOperation replaceRootWithPermissionStage = replaceRoot("$permissions");

        return newAggregation(
                filterByAssetIdStage,
                unwindPermissionsArrayStage,
                filterBySubjectIdStage,
                replaceRootWithPermissionStage);
    }
}
