package com.github.sujankumarmitra.assetservice.v1.dao;

import com.github.sujankumarmitra.assetservice.v1.model.AssetPermission;
import reactor.core.publisher.Mono;

/**
 * @author skmitra
 * @since Sep 25/09/21, 2021
 */
public interface AssetPermissionDao {

    Mono<Void> upsert(AssetPermission permission);

    Mono<AssetPermission> findOne(String assetId, String subjectId);
}
