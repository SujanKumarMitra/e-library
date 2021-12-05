package com.github.sujankumarmitra.libraryservice.v1.service;

import com.github.sujankumarmitra.libraryservice.v1.model.PackageTag;
import reactor.core.publisher.Mono;

/**
 * @author skmitra
 * @since Dec 05/12/21, 2021
 */
public interface PackageTagService {

    Mono<String> createTag(PackageTag tag);

    Mono<Void> updateTag(PackageTag tag);

    Mono<Void> deleteTag(String tagId);

}
