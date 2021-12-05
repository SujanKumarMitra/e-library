package com.github.sujankumarmitra.libraryservice.v1.service;

import com.github.sujankumarmitra.libraryservice.v1.model.BookTag;
import reactor.core.publisher.Mono;

/**
 * @author skmitra
 * @since Dec 05/12/21, 2021
 */
public interface BookTagService {

    Mono<String> createTag(BookTag tag);

    Mono<Void> updateTag(BookTag tag);

    Mono<Void> deleteTag(String tagId);

}
