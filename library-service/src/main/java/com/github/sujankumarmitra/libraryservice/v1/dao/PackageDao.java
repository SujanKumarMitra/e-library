package com.github.sujankumarmitra.libraryservice.v1.dao;

import com.github.sujankumarmitra.libraryservice.v1.model.Package;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author skmitra
 * @since Nov 22/11/21, 2021
 */
public interface PackageDao {

    Mono<String> createPackage(Package aPackage);

    Flux<Package> getPackages(int skip, int limit);

    Flux<Package> getPackagesByNameStartingWith(String prefix, int skip, int limit);

    Mono<Package> getPackage(String packageId);

    Mono<Void> updatePackage(Package aPackage);

    Mono<Void> deletePackage(String packageId);

}
