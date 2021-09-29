package com.github.mitrakumarsujan.notificationservice.v1.dao;

import com.github.mitrakumarsujan.notificationservice.v1.model.Notification;
import lombok.Builder;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author skmitra
 * @since Sep 29/09/21, 2021
 */
@Data
@Builder(builderMethodName = "newBuilder")
@Document(collection = "notifications")
public class MongoNotificationDocument extends Notification {
    @Id
    private final ObjectId objectId;
    @Indexed
    private final String consumerId;
    private final long createdAt;
    private final boolean acknowledged;
    private final String payload;

    public String getId() {
        return objectId == null ? null : objectId.toHexString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Notification)) return false;
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

}
