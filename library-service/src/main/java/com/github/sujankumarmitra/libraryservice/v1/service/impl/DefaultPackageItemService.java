package com.github.sujankumarmitra.libraryservice.v1.service.impl;

import com.github.sujankumarmitra.libraryservice.v1.dao.PackageItemDao;
import com.github.sujankumarmitra.libraryservice.v1.model.PackageItem;
import com.github.sujankumarmitra.libraryservice.v1.service.PackageItemService;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * @author skmitra
 * @since Dec 04/12/21, 2021
 */
@Service
@AllArgsConstructor
public class DefaultPackageItemService implements PackageItemService {

    @NonNull
    private final PackageItemDao itemDao;

    @Override
    public Mono<String> createItem(PackageItem item) {
        return itemDao.createItem(item);
    }

    @Override
    public Mono<Void> updateItem(PackageItem item) {
        return itemDao.updateItem(item);
    }

    @Override
    public Mono<Void> deleteItem(String itemId) {
        return itemDao.deleteById(itemId);
    }
}
