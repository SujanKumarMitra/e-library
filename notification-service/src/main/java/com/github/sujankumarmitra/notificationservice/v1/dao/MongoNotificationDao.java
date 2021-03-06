package com.github.sujankumarmitra.notificationservice.v1.dao;

import com.github.sujankumarmitra.notificationservice.v1.exception.NotificationNotFoundException;
import com.github.sujankumarmitra.notificationservice.v1.model.DefaultNotification;
import com.github.sujankumarmitra.notificationservice.v1.model.Notification;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;
import com.mongodb.reactivestreams.client.FindPublisher;
import com.mongodb.reactivestreams.client.MongoCollection;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.bson.BsonObjectId;
import org.bson.BsonValue;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.reactivestreams.Publisher;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Sorts.descending;
import static com.mongodb.client.model.Updates.set;

/**
 * @author skmitra
 * @since Sep 29/09/21, 2021
 */
@Repository
@AllArgsConstructor
public class MongoNotificationDao implements NotificationDao {

    public static final String CONSUMER_ID = "consumerId";
    public static final String ACKNOWLEDGED = "acknowledged";
    public static final String CREATED_AT = "createdAt";
    public static final String ID = "_id";
    public static final String PAYLOAD = "payload";
    @NonNull
    private final ReactiveMongoTemplate mongoTemplate;

    private Notification toPojo(Document document) {
        return DefaultNotification
                .newBuilder()
                .id(document.getObjectId(ID).toHexString())
                .payload(document.getString(PAYLOAD))
                .consumerId(document.getString(CONSUMER_ID))
                .acknowledged(document.getBoolean(ACKNOWLEDGED))
                .createdAt(document.getLong(CREATED_AT))
                .build();
    }

    @Override
    public Mono<String> insert(@NonNull Notification notification) {
        return getMongoCollection()
                .flatMapMany(collection -> insert(notification, collection))
                .next()
                .filter(InsertOneResult::wasAcknowledged)
                .map(InsertOneResult::getInsertedId)
                .map(BsonValue::asObjectId)
                .map(BsonObjectId::getValue)
                .map(ObjectId::toHexString);
    }

    @Override
    public Mono<Notification> findOne(String notificationId, String consumerId) {
        ObjectId objectId;
        try {
            objectId = new ObjectId(notificationId);
        } catch (IllegalArgumentException ex) {
            return Mono.empty();
        }


        return getMongoCollection()
                .flatMapMany(collection -> findOne(consumerId, objectId, collection))
                .next()
                .map(this::toPojo);
    }

    private Publisher<Document> findOne(String consumerId, ObjectId objectId, MongoCollection<Document> collection) {
        return collection.find(and(
                        eq(ID, objectId),
                        eq(CONSUMER_ID, consumerId)))
                .first();
    }

    private Publisher<InsertOneResult> insert(Notification notification, MongoCollection<Document> collection) {
        return collection.insertOne(
                new Document()
                        .append(CREATED_AT, notification.getCreatedAt())
                        .append(CONSUMER_ID, notification.getConsumerId())
                        .append(PAYLOAD, notification.getPayload())
                        .append(ACKNOWLEDGED, notification.isAcknowledged()));
    }

    @Override
    public Flux<Notification> find(@NonNull String consumerId, int count) {

        return getMongoCollection()
                .flatMapMany(collection -> find(consumerId, count, collection))
                .map(this::toPojo);
    }

    private Mono<MongoCollection<Document>> getMongoCollection() {
        return mongoTemplate
                .getCollection(
                        mongoTemplate.getCollectionName(MongoNotificationDocument.class));
    }

    private FindPublisher<Document> find(String consumerId, int count, MongoCollection<Document> collection) {
        return collection
                .find(eq(CONSUMER_ID, consumerId))
                .limit(count)
                .sort(descending(CREATED_AT, ID));
    }

    @Override
    public Flux<Notification> find(@NonNull String consumerId, @NonNull String lastNotificationId, int count) {
        ObjectId objectId;
        try {
            objectId = new ObjectId(lastNotificationId);
        } catch (IllegalArgumentException ex) {
//            invalid notification id
            return Flux.empty();
        }
        return getMongoCollection()
                .flatMapMany(collection -> find(consumerId, objectId, count, collection))
                .map(this::toPojo);

    }

    private FindPublisher<Document> find(String consumerId, ObjectId objectId, int count, MongoCollection<Document> collection) {
        return collection
                .find(and(
                        eq(CONSUMER_ID, consumerId),
                        lt(ID, objectId)))
                .limit(count)
                .sort(descending(CREATED_AT, ID));
    }

    @Override
    public Mono<Void> setAcknowledged(@NonNull String notificationId, @NonNull String consumerId) {
        final ObjectId objectId;
        try {
            objectId = new ObjectId(notificationId);
        } catch (IllegalArgumentException ex) {
            return Mono.error(new NotificationNotFoundException(notificationId));
        }


        return getMongoCollection()
                .flatMapMany(mongoCollection -> updateAcknowledged(objectId, consumerId, mongoCollection))
                .next()
                .handle(((result, sink) -> {
                    if (result.getMatchedCount() == 0)
                        sink.error(new NotificationNotFoundException(notificationId));
                    else
                        sink.complete();
                }));
    }

    private Publisher<UpdateResult> updateAcknowledged(ObjectId finalObjectId, String consumerId, MongoCollection<Document> mongoCollection) {
        return mongoCollection.updateOne(
                and(eq(ID, finalObjectId),
                        eq(CONSUMER_ID, consumerId)),
                set(ACKNOWLEDGED, true));
    }
}
