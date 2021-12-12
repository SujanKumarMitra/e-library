package com.github.sujankumarmitra.ebookprocessor.v1.service.impl;

import com.github.sujankumarmitra.ebookprocessor.v1.config.RemoteServiceRegistry;
import com.github.sujankumarmitra.ebookprocessor.v1.exception.RemoteServiceException;
import com.github.sujankumarmitra.ebookprocessor.v1.model.Asset;
import com.github.sujankumarmitra.ebookprocessor.v1.security.AuthenticationTokenExchangeFilterFunction;
import com.github.sujankumarmitra.ebookprocessor.v1.service.AssetServiceClient;
import lombok.NonNull;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.nio.file.Path;

import static org.springframework.http.HttpHeaders.LOCATION;
import static org.springframework.web.reactive.function.BodyExtractors.toMono;
import static org.springframework.web.reactive.function.BodyInserters.fromPublisher;
import static org.springframework.web.reactive.function.BodyInserters.fromResource;
import static reactor.core.publisher.Mono.just;

/**
 * @author skmitra
 * @since Dec 11/12/21, 2021
 */
@Service
public class RemoteServiceAssetServiceClient implements AssetServiceClient {
    @NonNull
    private final WebClient client;

    public RemoteServiceAssetServiceClient(WebClient.Builder builder,
                                           RemoteServiceRegistry serviceRegistry,
                                           AuthenticationTokenExchangeFilterFunction filterFunction) {
        this.client = builder
                .baseUrl(serviceRegistry.getService("asset-service").getBaseUrl())
                .filter(filterFunction)
                .build();
    }

    @Override
    public Mono<String> createAsset(Asset asset) {
        return client
                .post()
                .uri("/api/v1/assets")
                .body(fromPublisher(just(asset), Asset.class))
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError,
                        res -> res.body(toMono(String.class))
                                .map(body -> new RemoteServiceException(res.statusCode(), body)))
                .toBodilessEntity()
                .map(entity -> entity.getHeaders().getFirst(LOCATION));
    }

    @Override
    public Mono<Void> storeAsset(String assetId, Path objectPath) {
        return client
                .put()
                .uri("/api/v1/assets/{assetId}", assetId)
                .body(fromResource(new FileSystemResource(objectPath)))
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError,
                        res -> res.body(toMono(String.class))
                                .map(body -> new RemoteServiceException(res.statusCode(), body)))
                .toBodilessEntity()
                .then();
    }
}
