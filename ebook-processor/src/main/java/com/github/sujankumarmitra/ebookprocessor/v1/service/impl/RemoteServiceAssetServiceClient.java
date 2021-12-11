package com.github.sujankumarmitra.ebookprocessor.v1.service.impl;

import com.github.sujankumarmitra.ebookprocessor.v1.config.RemoteServiceRegistry;
import com.github.sujankumarmitra.ebookprocessor.v1.security.AuthenticationTokenExchangeFilterFunction;
import com.github.sujankumarmitra.ebookprocessor.v1.service.AssetServiceClient;
import lombok.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.nio.file.Path;
import java.util.UUID;

/**
 * @author skmitra
 * @since Dec 11/12/21, 2021
 */
@Service
public class RemoteServiceAssetServiceClient implements AssetServiceClient {
    @NonNull
    private final WebClient client;

    public RemoteServiceAssetServiceClient(WebClient.Builder builder, RemoteServiceRegistry serviceRegistry) {
        this.client = builder
                .baseUrl(serviceRegistry.getService("asset-service").getBaseUrl())
                .filter(new AuthenticationTokenExchangeFilterFunction())
                .build();
    }

    @Override
    public Mono<String> saveAsset(Path asset) {
//        TODO
        return Mono.just(UUID.randomUUID().toString());
    }
}
