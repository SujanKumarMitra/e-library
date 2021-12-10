package com.github.sujankumarmitra.libraryservice.v1.service;

import com.github.sujankumarmitra.libraryservice.v1.model.EBookSegment;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author skmitra
 * @since Dec 10/12/21, 2021
 */
public interface EBookSegmentService {
    Flux<EBookSegment> getSegmentsByEBookId(String ebookId, int pageNo);

    Mono<EBookSegment> getSegmentByBookIdAndIndex(String ebookId, int index);

    Mono<String> createSegment(EBookSegment ebookSegment);

    Mono<Void> deleteSegmentsByBookId(String ebookId);
}
