package com.github.sujankumarmitra.libraryservice.v1.dao;

import com.github.sujankumarmitra.libraryservice.v1.model.PackageItem;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.List;

/**
 * @author skmitra
 * @since Nov 22/11/21, 2021
 */
public interface PackageItemDao {

    default Mono<String> createItem(PackageItem packageItem) {
        return createItems(List.of(packageItem)).next();
    }

    Flux<String> createItems(Collection<? extends PackageItem> packageItems);

    Flux<PackageItem> getItemsByPackageId(String packageId);

    default Mono<Void> updateItem(PackageItem packageItem) {
        return updateItems(List.of(packageItem));
    }

    Mono<Void> updateItems(Collection<? extends PackageItem> packageItems);

    Mono<Void> deleteItemsByPackageId(String packageId);

    Mono<Void> deleteById(String packageItemId);
}
