package com.github.sujankumarmitra.assetservice.v1.service;

import com.github.sujankumarmitra.assetservice.v1.dao.AssetDao;
import com.github.sujankumarmitra.assetservice.v1.model.Asset;
import com.github.sujankumarmitra.assetservice.v1.model.AssetPermission;
import com.github.sujankumarmitra.assetservice.v1.util.SecurityUtil;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.github.sujankumarmitra.assetservice.v1.security.SecurityRoles.ROLE_LIBRARIAN;
import static java.lang.Boolean.TRUE;

/**
 * @author skmitra
 * @since Jan 30/01/22, 2022
 */
@Service
@Primary
@AllArgsConstructor
public class SecuredAssetPermissionService implements AssetPermissionService {

    @NonNull
    private final AssetPermissionService delegate;
    @NonNull
    private final AssetDao assetDao;

    @Override
    public Mono<Void> grantPermission(AssetPermission permission) {
        return assetDao.findOne(permission.getAssetId())
                .map(Asset::getLibraryId)
                .filterWhen(libraryId -> Flux.just(ROLE_LIBRARIAN)
                        .map(role -> libraryId + ":" + role)
                        .flatMap(SecurityUtil::hasAuthority))
                .then(delegate.grantPermission(permission));
    }

    @Override
    public Mono<Boolean> hasPermission(String assetId, String subjectId) {
        return assetDao.findOne(assetId)
                .map(Asset::getLibraryId)
                .map(libraryId -> libraryId + ":" + ROLE_LIBRARIAN)
                .flatMap(SecurityUtil::hasAuthority)
                .flatMap(hasAuthority -> {
                    if (hasAuthority.booleanValue())
                        return Mono.just(TRUE);
                    else
                        return delegate.hasPermission(assetId, subjectId);
                });
    }
}
