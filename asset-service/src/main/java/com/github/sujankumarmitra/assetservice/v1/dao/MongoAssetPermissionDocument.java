package com.github.sujankumarmitra.assetservice.v1.dao;

import com.github.sujankumarmitra.assetservice.v1.model.AssetPermission;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Transient;
import org.springframework.data.annotation.TypeAlias;

@TypeAlias("AssetPermission")
@Data
@Builder(builderMethodName = "newBuilder")
@NoArgsConstructor // required by MappingMongoConverter
@AllArgsConstructor // required by LombokBuilder
class MongoAssetPermissionDocument implements AssetPermission {
    @Transient
    private String assetId;
    private String subjectId;
    private long grantStartEpochMilliseconds;
    private long grantDurationInMilliseconds;
}