package com.github.sujankumarmitra.libraryservice.v1.dao;

import com.github.sujankumarmitra.libraryservice.v1.model.Tag;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;

/**
 * @author skmitra
 * @since Nov 22/11/21, 2021
 */
public interface TagDao {

    Flux<String> createTags(Collection<? extends Tag> tags);

    Flux<Tag> getTagsByBookId(String bookId);

    Mono<Void> updateTags(Collection<? extends Tag> tags);

    Mono<Void> deleteTagsByBookId(String bookId);

}
