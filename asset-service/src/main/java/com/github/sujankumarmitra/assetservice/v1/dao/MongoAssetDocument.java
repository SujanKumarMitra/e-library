package com.github.sujankumarmitra.assetservice.v1.dao;

import com.github.sujankumarmitra.assetservice.v1.model.AbstractAsset;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
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
@Builder(builderMethodName = "newBuilder")
@Getter
@Setter
class MongoAssetDocument extends AbstractAsset {
    @Id
    private String id;
    private String name;
    private Set<MongoAssetPermissionDocument> permissions;
}
