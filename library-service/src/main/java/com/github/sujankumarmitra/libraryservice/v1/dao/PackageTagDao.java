package com.github.sujankumarmitra.libraryservice.v1.dao;

import com.github.sujankumarmitra.libraryservice.v1.model.PackageTag;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.List;

/**
 * @author skmitra
 * @since Nov 22/11/21, 2021
 */
public interface PackageTagDao {

    default Mono<String> createTag(PackageTag tag) {
        return createTags(List.of(tag)).next();
    }

    Flux<String> createTags(Collection<? extends PackageTag> tags);

    Flux<PackageTag> getTagsByPackageId(String bookId);

    default Mono<Void> updateTag(PackageTag tag) {
        return updateTags(List.of(tag));
    }

    Mono<Void> updateTags(Collection<? extends PackageTag> tags);

    Mono<Void> deleteTagsByPackageId(String bookId);

    Mono<Void> deleteTagById(String tagId);
}
