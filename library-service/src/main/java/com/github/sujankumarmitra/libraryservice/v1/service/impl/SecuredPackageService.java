package com.github.sujankumarmitra.libraryservice.v1.service.impl;

import com.github.sujankumarmitra.libraryservice.v1.dao.PackageDao;
import com.github.sujankumarmitra.libraryservice.v1.model.Package;
import com.github.sujankumarmitra.libraryservice.v1.service.PackageService;
import com.github.sujankumarmitra.libraryservice.v1.util.SecurityUtil;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.context.annotation.Primary;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.github.sujankumarmitra.libraryservice.v1.security.SecurityConstants.*;

/**
 * @author skmitra
 * @since Jan 27/01/22, 2022
 */
@Service
@Primary
@AllArgsConstructor
public class SecuredPackageService implements PackageService {

    @NonNull
    private final PackageService delegate;
    @NonNull
    private final PackageDao packageDao;

    @Override
    @PreAuthorize("hasAnyAuthority(" +
            "#aPackage.libraryId + ':" + ROLE_TEACHER + "', " +
            "#aPackage.libraryId + ':" + ROLE_LIBRARIAN + "')")
    public Mono<String> createPackage(Package aPackage) {
        return delegate.createPackage(aPackage);
    }

    @Override
    @PreAuthorize("hasAnyAuthority(" +
            "#libraryId + ':" + ROLE_STUDENT + "', " +
            "#libraryId + ':" + ROLE_TEACHER + "', " +
            "#libraryId + ':" + ROLE_LIBRARIAN + "')")
    public Flux<Package> getPackages(String libraryId, int pageNo) {
        return delegate.getPackages(libraryId, pageNo);
    }

    @Override
    @PreAuthorize("hasAnyAuthority(" +
            "#libraryId + ':" + ROLE_STUDENT + "', " +
            "#libraryId + ':" + ROLE_TEACHER + "', " +
            "#libraryId + ':" + ROLE_LIBRARIAN + "')")
    public Flux<Package> getPackagesByName(String libraryId, String namePrefix, int pageNo) {
        return delegate.getPackagesByName(libraryId, namePrefix, pageNo);
    }

    @Override
    public Mono<Package> getPackage(String packageId) {
        return delegate
                .getPackage(packageId)
                .filterWhen(aPackage -> Flux.just(ROLE_STUDENT, ROLE_TEACHER, ROLE_LIBRARIAN)
                        .map(role -> aPackage.getLibraryId() + ":" + role)
                        .flatMap(SecurityUtil::hasAuthority));
    }

    @Override
    @PreAuthorize("hasAnyAuthority(" +
            "#aPackage.libraryId + ':" + ROLE_TEACHER + "', " +
            "#aPackage.libraryId + ':" + ROLE_LIBRARIAN + "')")
    public Mono<Void> updatePackage(Package aPackage) {
        return delegate.updatePackage(aPackage);
    }

    @Override
    public Mono<Void> deletePackage(String packageId) {
        return packageDao
                .getPackage(packageId)
                .map(Package::getLibraryId)
                .flatMapMany(libraryId -> Flux.just(ROLE_LIBRARIAN, ROLE_TEACHER)
                        .map(roleId -> libraryId + ":" + roleId)
                        .flatMap(SecurityUtil::hasAuthority))
                .filter(Boolean::booleanValue)
                .switchIfEmpty(Mono.error(() -> new AccessDeniedException("Denied")))
                .flatMap(alwaysTrue -> delegate.deletePackage(packageId))
                .then();
    }

}
