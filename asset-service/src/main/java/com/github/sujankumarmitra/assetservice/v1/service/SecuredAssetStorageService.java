package com.github.sujankumarmitra.assetservice.v1.service;

import com.github.sujankumarmitra.assetservice.v1.dao.AssetDao;
import com.github.sujankumarmitra.assetservice.v1.model.Asset;
import com.github.sujankumarmitra.assetservice.v1.model.StoredAsset;
import com.github.sujankumarmitra.assetservice.v1.util.SecurityUtil;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.github.sujankumarmitra.assetservice.v1.security.SecurityRoles.ROLE_LIBRARIAN;

/**
 * @author skmitra
 * @since Jan 30/01/22, 2022
 */
@Service
@Primary
@AllArgsConstructor
public class SecuredAssetStorageService implements AssetStorageService {

    @NonNull
    private final AssetStorageService delegate;
    @NonNull
    private final AssetPermissionService assetPermissionService;
    @NonNull
    private final AssetDao assetDao;

    @Override
    public Mono<Void> storeAsset(String assetId, Flux<DataBuffer> dataBuffers) {
        return assetDao.findOne(assetId)
                .map(Asset::getLibraryId)
                .map(libraryId -> libraryId + ":" + ROLE_LIBRARIAN)
                .filterWhen(SecurityUtil::hasAuthority)
                .switchIfEmpty(Mono.error(() -> new AccessDeniedException("Denied")))
                .then(delegate.storeAsset(assetId, dataBuffers));
    }

    @Override
    public Mono<StoredAsset> retrieveAsset(String assetId) {
        return SecurityUtil
                .getCurrentUser()
                .map(Authentication::getName)
                .filterWhen(subjectId -> assetPermissionService.hasPermission(assetId, subjectId))
                .switchIfEmpty(Mono.error(() -> new AccessDeniedException("Denied")))
                .then(delegate.retrieveAsset(assetId));
    }

    @Override
    public Mono<Void> purgeAsset(String assetId) {
        // Internal API, no auth required
        return delegate.purgeAsset(assetId);
    }
}
