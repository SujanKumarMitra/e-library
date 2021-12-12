package com.github.sujankumarmitra.assetservice.v1.service;

import com.github.sujankumarmitra.assetservice.v1.dao.AssetDao;
import com.github.sujankumarmitra.assetservice.v1.dao.AssetPermissionDao;
import com.github.sujankumarmitra.assetservice.v1.exception.AssetNotFoundException;
import com.github.sujankumarmitra.assetservice.v1.model.Asset;
import com.github.sujankumarmitra.assetservice.v1.model.AssetPermission;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import static java.lang.Boolean.FALSE;


/**
 * @author skmitra
 * @since Sep 25/09/21, 2021
 */
@Service
@AllArgsConstructor
public class DefaultAssetPermissionService implements AssetPermissionService {

    @NonNull
    private final AssetDao assetDao;
    @NonNull
    private final AssetPermissionDao permissionDao;

    @Override
    public Mono<Void> grantPermission(AssetPermission permission) {
        return permissionDao.upsert(permission);
    }

    @Override
    public Mono<Boolean> hasPermission(String assetId, String subjectId) {
        long currentTimestamp = System.currentTimeMillis();

        return assetDao.findOne(assetId)
                .switchIfEmpty(Mono.error(() -> new AssetNotFoundException(assetId)))
                .map(Asset::getOwnerId)
                .map(ownerId -> ownerId.equals(subjectId))
                .filter(Boolean::booleanValue)
                .switchIfEmpty(permissionDao
                        .findOne(assetId, subjectId)
                        .map(permission -> checkPermission(currentTimestamp, permission))
                        .switchIfEmpty(Mono.fromSupplier(() -> FALSE)));

    }

    private boolean checkPermission(long currentTimestamp, AssetPermission permission) {

        if (currentTimestamp < permission.getGrantStartEpochMilliseconds()) return false;
        if (permission.getGrantDurationInMilliseconds() == AssetPermission.INFINITE_GRANT_DURATION) return true;

        long permissionExpiryTimestamp = 0L;

        permissionExpiryTimestamp += permission.getGrantStartEpochMilliseconds();
        permissionExpiryTimestamp += permission.getGrantDurationInMilliseconds();

        return currentTimestamp <= permissionExpiryTimestamp;

    }

}