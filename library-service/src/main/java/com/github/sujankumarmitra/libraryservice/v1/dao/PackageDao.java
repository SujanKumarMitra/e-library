package com.github.sujankumarmitra.libraryservice.v1.dao;

import com.github.sujankumarmitra.libraryservice.v1.model.Package;
import reactor.core.publisher.Mono;

/**
 * @author skmitra
 * @since Nov 22/11/21, 2021
 */
public interface PackageDao {

    Mono<String> createPackage(Package _package);

    Mono<Package> getPackage(String packageId);

    Mono<Void> updatePackage(Package _package);

    Mono<Void> deletePackage(String packageId);

}
