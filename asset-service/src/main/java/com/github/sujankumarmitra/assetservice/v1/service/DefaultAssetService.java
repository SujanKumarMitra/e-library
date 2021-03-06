package com.github.sujankumarmitra.assetservice.v1.service;

import com.github.sujankumarmitra.assetservice.v1.dao.AssetDao;
import com.github.sujankumarmitra.assetservice.v1.model.Asset;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author skmitra
 * @since Sep 26/09/21, 2021
 */
@Service
@AllArgsConstructor
public class DefaultAssetService implements AssetService {

    @NonNull
    private final AssetDao assetDao;
    @NonNull
    private final AssetStorageService assetStorageService;

    @Override
    public Mono<Asset> createAsset(Asset asset) {
        return assetDao.insert(asset);
    }

    @Override
    public Mono<Void> deleteAsset(String assetId) {
        return assetDao
                .delete(assetId)
                .then(assetStorageService.purgeAsset(assetId));
    }

    @Override
    public Mono<Asset> getAsset(String assetId) {
        return assetDao.findOne(assetId);
    }

    @Override
    public Flux<Asset> getAssets(String libraryId) {
        return assetDao.findByLibraryId(libraryId);
    }
}