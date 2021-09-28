package com.github.sujankumarmitra.assetservice.v1.dao;

import com.github.sujankumarmitra.assetservice.v1.model.Asset;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;


/**
 * @author skmitra
 * @since Sep 25/09/21, 2021
 */
@Document("assets")
@TypeAlias("Asset")
@Getter
@Setter
class MongoAssetDocument extends Asset {
    @Id
    private ObjectId id;
    private String name;
    private Set<MongoAssetPermissionDocument> permissions;

    MongoAssetDocument(ObjectId id, String name, Set<MongoAssetPermissionDocument> permissions) {
        this.id = id;
        this.name = name;
        this.permissions = permissions;
    }

    public static MongoAssetDocumentBuilder newBuilder() {
        return new MongoAssetDocumentBuilder();
    }

    @Override
    public String getId() {
        return id.toString();
    }

    public ObjectId getObjectId() {
        return id;
    }

    public static class MongoAssetDocumentBuilder {
        private ObjectId id;
        private String name;
        private Set<MongoAssetPermissionDocument> permissions;

        MongoAssetDocumentBuilder() {
        }

        public MongoAssetDocumentBuilder id(ObjectId id) {
            this.id = id;
            return this;
        }

        public MongoAssetDocumentBuilder name(String name) {
            this.name = name;
            return this;
        }

        public MongoAssetDocumentBuilder permissions(Set<MongoAssetPermissionDocument> permissions) {
            this.permissions = permissions;
            return this;
        }

        public MongoAssetDocument build() {
            return new MongoAssetDocument(id, name, permissions);
        }

        public String toString() {
            return "MongoAssetDocument.MongoAssetDocumentBuilder(id=" + this.id + ", name=" + this.name + ", permissions=" + this.permissions + ")";
        }
    }
}
