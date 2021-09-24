package com.github.sujankumarmitra.assetservice.v1.service;

import com.github.sujankumarmitra.assetservice.v1.model.AssetPermission;
import reactor.core.publisher.Mono;

/**
 * @author skmitra
 * @since Sep 24/09/21, 2021
 */
public interface AssetPermissionService {

    Mono<Void> grantPermission(AssetPermission request);

    Mono<Boolean> hasPermission(String assetId, String subjectId);
}
