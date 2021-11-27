package com.github.sujankumarmitra.libraryservice.v1.dao;

import com.github.sujankumarmitra.libraryservice.v1.model.PackageTag;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;

/**
 * @author skmitra
 * @since Nov 22/11/21, 2021
 */
public interface PackageTagDao {

    Flux<String> createTags(Collection<? extends PackageTag> tags);

    Flux<PackageTag> getTagsByPackageId(String bookId);

    Mono<Void> updateTags(Collection<? extends PackageTag> tags);

    Mono<Void> deleteTagsByPackageId(String bookId);

}
