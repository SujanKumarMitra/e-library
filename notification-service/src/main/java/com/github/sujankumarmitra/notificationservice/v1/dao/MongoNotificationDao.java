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
    public Mono<String> insert(Notification notification) {
        return getMongoCollection()
                .flatMapMany(collection -> insert(notification, collection))
                .next()
                .filter(InsertOneResult::wasAcknowledged)
                .map(InsertOneResult::getInsertedId)
                .map(BsonValue::asObjectId)
                .map(BsonObjectId::getValue)
                .map(ObjectId::toHexString);
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
    public Flux<Notification> find(String consumerId, int count) {

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
                .sort(descending(CREATED_AT));
    }

    @Override
    public Flux<Notification> find(String consumerId, String lastNotificationId, int count) {
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
                        gt(ID, objectId)))
                .limit(count)
                .sort(descending(CREATED_AT));
    }

    @Override
    public Mono<Void> setAcknowledged(String notificationId) {
        final ObjectId objectId;
        try {
            objectId = new ObjectId(notificationId);
        } catch (IllegalArgumentException ex) {
            return Mono.error(new NotificationNotFoundException(notificationId));
        }


        return getMongoCollection()
                .flatMapMany(mongoCollection -> updateAcknowledged(objectId, mongoCollection))
                .next()
                .handle(((result, sink) -> {
                    if (result.getMatchedCount() == 0)
                        sink.error(new NotificationNotFoundException(notificationId));
                    else
                        sink.complete();
                }));
    }

    private Publisher<UpdateResult> updateAcknowledged(ObjectId finalObjectId, MongoCollection<Document> mongoCollection) {
        return mongoCollection.updateOne(eq(ID, finalObjectId), set(ACKNOWLEDGED, true));
    }
}
