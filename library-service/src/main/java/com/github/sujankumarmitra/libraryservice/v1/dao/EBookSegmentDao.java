package com.github.sujankumarmitra.libraryservice.v1.dao;

import com.github.sujankumarmitra.libraryservice.v1.model.EBookSegment;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author skmitra
 * @since Dec 10/12/21, 2021
 */
public interface EBookSegmentDao {
    <E extends EBookSegment> Flux<E> getSegmentsByBookId(String ebookId, int skip, int limit);

    <E extends EBookSegment> Mono<E> getSegmentByBookIdAndIndex(String ebookId, int index);

    Mono<String> createSegment(EBookSegment ebookSegment);

    Mono<Void> deleteAllSegmentsByBookId(String ebookId);
}
