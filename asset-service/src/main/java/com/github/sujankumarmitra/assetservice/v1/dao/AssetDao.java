package com.github.sujankumarmitra.assetservice.v1.dao;

import com.github.sujankumarmitra.assetservice.v1.model.Asset;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author skmitra
 * @since Sep 25/09/21, 2021
 */
public interface AssetDao {
    Mono<Asset> insert(Asset asset);

    Mono<Void> delete(String assetId);

    Mono<Asset> findOne(String assetId);

    Flux<Asset> findByLibraryId(String libraryId);
}
