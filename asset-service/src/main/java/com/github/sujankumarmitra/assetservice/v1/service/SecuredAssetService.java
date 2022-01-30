package com.github.sujankumarmitra.assetservice.v1.service;

import com.github.sujankumarmitra.assetservice.v1.dao.AssetDao;
import com.github.sujankumarmitra.assetservice.v1.model.Asset;
import com.github.sujankumarmitra.assetservice.v1.util.SecurityUtil;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.context.annotation.Primary;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import static com.github.sujankumarmitra.assetservice.v1.security.SecurityRoles.ROLE_LIBRARIAN;

/**
 * @author skmitra
 * @since Jan 30/01/22, 2022
 */
@Service
@Primary
@AllArgsConstructor
public class SecuredAssetService implements AssetService {

    @NonNull
    private final AssetService delegate;
    @NonNull
    private final AssetDao assetDao;

    @Override
    @PreAuthorize("hasAuthority(#asset.libraryId + ':" + ROLE_LIBRARIAN + "')")
    public Mono<Asset> createAsset(Asset asset) {
        return delegate.createAsset(asset);
    }

    @Override
    public Mono<Void> deleteAsset(String assetId) {
        return assetDao
                .findOne(assetId)
                .map(Asset::getLibraryId)
                .map(libraryId -> libraryId + ":" + ROLE_LIBRARIAN)
                .filterWhen(SecurityUtil::hasAuthority)
                .switchIfEmpty(Mono.error(() -> new AccessDeniedException("Denied")))
                .then(delegate.deleteAsset(assetId));
    }

    @Override
    public Mono<Asset> getAsset(String assetId) {
        return delegate.getAsset(assetId);
    }

}
