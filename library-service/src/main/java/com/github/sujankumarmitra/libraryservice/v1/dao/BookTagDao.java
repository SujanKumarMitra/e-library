package com.github.sujankumarmitra.libraryservice.v1.dao;

import com.github.sujankumarmitra.libraryservice.v1.model.BookTag;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.List;

/**
 * @author skmitra
 * @since Nov 22/11/21, 2021
 */
public interface BookTagDao {

    default Mono<String> createTag(BookTag tag) {
        return createTags(List.of(tag)).next();
    }

    Flux<String> createTags(Collection<? extends BookTag> tags);

    Flux<BookTag> getTagsByBookId(String bookId);

    default Mono<Void> updateTag(BookTag tag) {
        return updateTags(List.of(tag));
    }

    Mono<Void> updateTags(Collection<? extends BookTag> tags);

    Mono<Void> deleteTagById(String tagId);

    Mono<Void> deleteTagsByBookId(String bookId);

}
