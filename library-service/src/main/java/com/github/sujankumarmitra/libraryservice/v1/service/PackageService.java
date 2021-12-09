package com.github.sujankumarmitra.libraryservice.v1.service;

import com.github.sujankumarmitra.libraryservice.v1.model.Package;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author skmitra
 * @since Dec 04/12/21, 2021
 */
public interface PackageService {

    Mono<String> createPackage(Package aPackage);

    Flux<Package> getPackages(int pageNo);

    Flux<Package> getPackagesByName(String namePrefix, int pageNo);

    Mono<Void> updatePackage(Package aPackage);

    Mono<Void> deletePackage(String packageId);

}
