package com.github.sujankumarmitra.assetservice.v1.service;

import com.github.sujankumarmitra.assetservice.v1.dao.AssetPermissionDao;
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
    private final AssetPermissionDao permissionDao;

    @Override
    public Mono<Void> grantPermission(AssetPermission permission) {
        return permissionDao.upsert(permission);
    }

    @Override
    public Mono<Boolean> hasPermission(String assetId, String subjectId) {
        long currentTimestamp = System.currentTimeMillis();

        return permissionDao.findOne(assetId, subjectId)
                .map(permission -> checkPermission(currentTimestamp, permission))
                .switchIfEmpty(Mono.just(FALSE));

    }

    private boolean checkPermission(long currentTimestamp, AssetPermission permission) {
        if (permission.getGrantDurationInMilliseconds() == AssetPermission.INFINITE_GRANT_DURATION) return true;

        long permissionExpiryTimestamp = 0L;

        permissionExpiryTimestamp += permission.getGrantStartEpochMilliseconds();
        permissionExpiryTimestamp += permission.getGrantDurationInMilliseconds();

        return currentTimestamp <= permissionExpiryTimestamp;
    }

}