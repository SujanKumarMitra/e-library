package com.github.sujankumarmitra.ebookprocessor.v1.service;

import reactor.core.publisher.Mono;

import java.nio.file.Path;

/**
 * @author skmitra
 * @since Dec 11/12/21, 2021
 */
public interface AssetServiceClient {

    Mono<String> saveAsset(Path asset);
}
