package com.github.sujankumarmitra.ebookprocessor.v1.service;

import com.github.sujankumarmitra.ebookprocessor.v1.model.Asset;
import reactor.core.publisher.Mono;

import java.nio.file.Path;

/**
 * @author skmitra
 * @since Dec 11/12/21, 2021
 */
public interface AssetServiceClient {

    Mono<String> createAsset(Asset asset);

    Mono<Void> storeAsset(String assetId, Path objectPath);
}
