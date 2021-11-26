package com.github.sujankumarmitra.libraryservice.v1.dao;

import com.github.sujankumarmitra.libraryservice.v1.model.PackageItem;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;

/**
 * @author skmitra
 * @since Nov 22/11/21, 2021
 */
public interface PackageItemDao {

    Flux<String> createPackageItems(Collection<? extends PackageItem> packageItems);

    Flux<PackageItem> getPackageItemsByPackageId(String packageId);

    Mono<Void> updatePackageItems(Collection<? extends PackageItem> tags);

    Mono<Void> deletePackageItemsByPackageId(String packageId);

}
