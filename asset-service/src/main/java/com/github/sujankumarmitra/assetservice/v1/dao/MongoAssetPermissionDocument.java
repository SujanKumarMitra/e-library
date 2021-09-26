package com.github.sujankumarmitra.assetservice.v1.dao;

import com.github.sujankumarmitra.assetservice.v1.model.AssetPermission;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Transient;
import org.springframework.data.annotation.TypeAlias;

@Data
@Builder(builderMethodName = "newBuilder")
@TypeAlias("AssetPermission")
class MongoAssetPermissionDocument implements AssetPermission {
    @Transient
    private String assetId;
    private String subjectId;
    private long grantStartEpochSecond;
    private long grantDurationInMillis;
}