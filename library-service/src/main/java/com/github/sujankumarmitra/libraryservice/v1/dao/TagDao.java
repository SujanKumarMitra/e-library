package com.github.sujankumarmitra.libraryservice.v1.dao;

import com.github.sujankumarmitra.libraryservice.v1.model.Tag;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Set;

/**
 * @author skmitra
 * @since Nov 22/11/21, 2021
 */
public interface TagDao {

    Mono<Void> insertTags(Set<? extends Tag> tags);

    Flux<Tag> selectTags(String bookId);

    Mono<Void> updateTags(Set<? extends Tag> tags);
}
