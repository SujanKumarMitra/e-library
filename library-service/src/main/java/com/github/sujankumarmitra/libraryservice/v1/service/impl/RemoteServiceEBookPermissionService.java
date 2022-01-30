package com.github.sujankumarmitra.libraryservice.v1.service.impl;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.sujankumarmitra.libraryservice.v1.config.RemoteServiceRegistry;
import com.github.sujankumarmitra.libraryservice.v1.dao.EBookSegmentDao;
import com.github.sujankumarmitra.libraryservice.v1.model.EBookPermission;
import com.github.sujankumarmitra.libraryservice.v1.model.EBookSegment;
import com.github.sujankumarmitra.libraryservice.v1.security.AuthenticationTokenExchangeFilterFunction;
import com.github.sujankumarmitra.libraryservice.v1.service.EBookPermissionService;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.reactive.function.BodyInserters.fromPublisher;
import static reactor.core.publisher.Mono.just;

/**
 * @author skmitra
 * @since Dec 12/12/21, 2021
 */
@Primary
@Service
@Slf4j
public class RemoteServiceEBookPermissionService implements EBookPermissionService {
    public static final String GRANT_PERMISSIONS_URI = "/api/assets/{assetId}/permissions";
    @NonNull
    private final EBookSegmentDao eBookSegmentDao;
    @NonNull
    private final WebClient webClient;

    public RemoteServiceEBookPermissionService(EBookSegmentDao eBookSegmentDao,
                                               WebClient.Builder builder,
                                               RemoteServiceRegistry serviceRegistry,
                                               AuthenticationTokenExchangeFilterFunction filterFunction) {
        this.eBookSegmentDao = eBookSegmentDao;
        this.webClient = builder
                .baseUrl(serviceRegistry.getService("asset-service").getBaseUrl())
                .defaultHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .filter(filterFunction)
                .build();
    }

    @Override
    public Mono<Void> assignPermission(EBookPermission ebookPermission) {
        return eBookSegmentDao
                .getSegmentsByBookId(ebookPermission.getBookId())
                .map(EBookSegment::getAssetId)
                .map(assetId -> mapToAssetPermission(assetId, ebookPermission))
                .flatMap(this::assignPermissionToSegmentAsset)
                .then();
    }

    private AssetPermission mapToAssetPermission(String assetId, EBookPermission ebookPermission) {
        AssetPermission permission = new AssetPermission();

        permission.assetId = assetId;
        permission.subjectId = ebookPermission.getUserId();
        permission.grantStartEpochMilliseconds = ebookPermission.getStartTimeInEpochMilliseconds();
        permission.grantDurationInMilliseconds = ebookPermission.getDurationInMilliseconds();

        return permission;
    }

    protected Mono<Void> assignPermissionToSegmentAsset(AssetPermission permission) {
        return webClient
                .patch()
                .uri(GRANT_PERMISSIONS_URI, permission.assetId)
                .body(fromPublisher(just(permission), AssetPermission.class))
                .retrieve()
                .toBodilessEntity()
                .then();
    }


    @Getter
    @Setter
    protected static class AssetPermission {
        @JsonIgnore
        String assetId;
        String subjectId;
        Long grantStartEpochMilliseconds;
        Long grantDurationInMilliseconds;
    }
}
