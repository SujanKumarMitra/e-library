package com.github.sujankumarmitra.libraryservice.v1.service;

import com.github.sujankumarmitra.libraryservice.v1.model.PackageItem;
import reactor.core.publisher.Mono;

/**
 * @author skmitra
 * @since Dec 04/12/21, 2021
 */
public interface PackageItemService {
    Mono<String> createItem(PackageItem item);

    Mono<Void> updateItem(PackageItem item);

    Mono<Void> deleteItem(String itemId);
}
