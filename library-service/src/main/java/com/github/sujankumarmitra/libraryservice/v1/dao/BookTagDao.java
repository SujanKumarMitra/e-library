package com.github.sujankumarmitra.libraryservice.v1.dao;

import com.github.sujankumarmitra.libraryservice.v1.model.BookTag;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;

/**
 * @author skmitra
 * @since Nov 22/11/21, 2021
 */
public interface BookTagDao {

    Flux<String> createTags(Collection<? extends BookTag> tags);

    Flux<BookTag> getTagsByBookId(String bookId);

    Mono<Void> updateTags(Collection<? extends BookTag> tags);

    Mono<Void> deleteTagsByBookId(String bookId);

}
