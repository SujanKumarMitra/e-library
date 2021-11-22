package com.github.sujankumarmitra.libraryservice.v1.dao;

import com.github.sujankumarmitra.libraryservice.v1.model.Tag;
import reactor.core.publisher.Mono;

import java.util.Set;

/**
 * @author skmitra
 * @since Nov 22/11/21, 2021
 */
public interface TagDao {

    Mono<Void> insertTags(Set<? extends Tag> tags);

    Mono<Void> updateTags(Set<? extends Tag> tags);
}
