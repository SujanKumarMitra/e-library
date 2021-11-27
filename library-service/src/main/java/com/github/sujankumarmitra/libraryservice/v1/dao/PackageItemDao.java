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

    Flux<String> createItems(Collection<? extends PackageItem> packageItems);

    Flux<PackageItem> getItemsByPackageId(String packageId);

    Mono<Void> updateItems(Collection<? extends PackageItem> packageItems);

    Mono<Void> deleteItemsByPackageId(String packageId);

}
