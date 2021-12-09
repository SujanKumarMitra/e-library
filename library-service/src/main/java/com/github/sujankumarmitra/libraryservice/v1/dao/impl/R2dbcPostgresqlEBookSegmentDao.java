package com.github.sujankumarmitra.libraryservice.v1.dao.impl;

import com.github.sujankumarmitra.libraryservice.v1.dao.EBookSegmentDao;
import com.github.sujankumarmitra.libraryservice.v1.dao.impl.entity.R2dbcEBookSegment;
import com.github.sujankumarmitra.libraryservice.v1.model.EBookSegment;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author skmitra
 * @since Dec 10/12/21, 2021
 */
@Repository
@Slf4j
@AllArgsConstructor
// TODO implement me
public class R2dbcPostgresqlEBookSegmentDao implements EBookSegmentDao {
    @Override
    @SuppressWarnings("unchecked")
    public Flux<R2dbcEBookSegment> getSegmentsByBookId(String ebookId, int skip, int limit) {
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Mono<R2dbcEBookSegment> getSegmentByBookIdAndIndex(String ebookId, int index) {
        return null;
    }

    @Override
    public Mono<String> createSegment(EBookSegment ebookSegment) {
        return null;
    }

    @Override
    public Mono<Void> deleteAllSegmentsByBookId(String ebookId) {
        return null;
    }
}
