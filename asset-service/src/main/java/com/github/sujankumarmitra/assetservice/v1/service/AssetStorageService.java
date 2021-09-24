package com.github.sujankumarmitra.assetservice.v1.service;

import com.github.sujankumarmitra.assetservice.v1.model.StoredAsset;
import org.springframework.core.io.buffer.DataBuffer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author skmitra
 * @since Sep 24/09/21, 2021
 */
public interface AssetStorageService {
    Mono<Void> storeAsset(String assetId, Flux<DataBuffer> dataBuffers);

    Mono<StoredAsset> retrieveAsset(String assetId);

    Mono<Void> purgeAsset(String assetId);
}
