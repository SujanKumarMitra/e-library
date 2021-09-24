package com.github.sujankumarmitra.assetservice.v1.service;

import com.github.sujankumarmitra.assetservice.v1.model.Asset;
import reactor.core.publisher.Mono;

/**
 * @author skmitra
 * @since Sep 24/09/21, 2021
 */
public interface AssetService {

    Mono<Asset> createAsset(Asset asset);

    Mono<Void> deleteAsset(String assetId);

    Mono<Asset> getAsset(String assetId);
}
