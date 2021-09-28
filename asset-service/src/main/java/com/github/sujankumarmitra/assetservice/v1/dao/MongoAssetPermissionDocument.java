package com.github.sujankumarmitra.assetservice.v1.dao;

import com.github.sujankumarmitra.assetservice.v1.model.AssetPermission;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Transient;
import org.springframework.data.annotation.TypeAlias;

@TypeAlias("AssetPermission")
@Data
@Builder(builderMethodName = "newBuilder")
@NoArgsConstructor // required by MappingMongoConverter
@AllArgsConstructor // required by LombokBuilder
class MongoAssetPermissionDocument extends AssetPermission {
    @Transient
    private ObjectId assetId;
    private String subjectId;
    private long grantStartEpochMilliseconds;
    private long grantDurationInMilliseconds;

    @Override
    public String getAssetId() {
        return assetId == null ? null : assetId.toString();
    }

    public void setAssetId(String assetId) {
        this.assetId = new ObjectId(assetId);
    }

    public ObjectId getAssetObjectId() {
        return assetId;
    }
}