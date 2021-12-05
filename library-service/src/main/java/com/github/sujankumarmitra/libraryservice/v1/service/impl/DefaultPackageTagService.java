package com.github.sujankumarmitra.libraryservice.v1.service.impl;

import com.github.sujankumarmitra.libraryservice.v1.dao.PackageTagDao;
import com.github.sujankumarmitra.libraryservice.v1.model.PackageTag;
import com.github.sujankumarmitra.libraryservice.v1.service.PackageTagService;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * @author skmitra
 * @since Dec 05/12/21, 2021
 */
@Service
@AllArgsConstructor
public class DefaultPackageTagService implements PackageTagService {

    @NonNull
    private final PackageTagDao packageTagDao;

    @Override
    public Mono<String> createTag(PackageTag tag) {
        return packageTagDao.createTag(tag);
    }

    @Override
    public Mono<Void> updateTag(PackageTag tag) {
        return packageTagDao.updateTag(tag);
    }

    @Override
    public Mono<Void> deleteTag(String tagId) {
        return packageTagDao.deleteTagById(tagId);
    }
}
