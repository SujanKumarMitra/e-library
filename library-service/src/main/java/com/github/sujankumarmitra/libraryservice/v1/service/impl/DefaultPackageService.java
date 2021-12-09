package com.github.sujankumarmitra.libraryservice.v1.service.impl;

import com.github.sujankumarmitra.libraryservice.v1.config.PagingProperties;
import com.github.sujankumarmitra.libraryservice.v1.dao.PackageDao;
import com.github.sujankumarmitra.libraryservice.v1.model.Package;
import com.github.sujankumarmitra.libraryservice.v1.service.PackageService;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author skmitra
 * @since Dec 04/12/21, 2021
 */
@Service
@AllArgsConstructor
public class DefaultPackageService implements PackageService {

    @NonNull
    private final PackageDao packageDao;
    @NonNull
    private final PagingProperties pagingProperties;

    @Override
    public Mono<String> createPackage(Package aPackage) {
        return packageDao.createPackage(aPackage);
    }

    @Override
    public Flux<Package> getPackages(int pageNo) {
        int pageSize = pagingProperties.getDefaultPageSize();
        int skip = pageNo * pageSize;

        return packageDao
                .getPackages(skip, pageSize);
    }

    @Override
    public Flux<Package> getPackagesByName(String packageName, int pageNo) {

        int pageSize = pagingProperties.getDefaultPageSize();
        int skip = pageNo * pageSize;

        return packageDao
                .getPackagesByNameStartsWith(packageName, skip, pageSize);
    }

    @Override
    public Mono<Void> updatePackage(Package aPackage) {
        return packageDao.updatePackage(aPackage);
    }

    @Override
    public Mono<Void> deletePackage(String packageId) {
        return packageDao.deletePackage(packageId);
    }
}
