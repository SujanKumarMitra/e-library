package com.github.sujankumarmitra.libraryservice.v1.dao.impl;

import com.github.sujankumarmitra.libraryservice.v1.dao.EBookSegmentDao;
import com.github.sujankumarmitra.libraryservice.v1.dao.impl.entity.R2dbcEBookSegment;
import com.github.sujankumarmitra.libraryservice.v1.model.EBookSegment;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;

/**
 * @author skmitra
 * @since Dec 10/12/21, 2021
 */
@Repository
@Slf4j
@AllArgsConstructor
// TODO implement me
public class R2dbcPostgresqlEBookSegmentDao implements EBookSegmentDao {
    @NonNull
    private final R2dbcEntityTemplate entityTemplate;

    @Override
    @SuppressWarnings("unchecked")
    public Flux<R2dbcEBookSegment> getSegmentsByBookId(@NonNull String ebookId, int skip, int limit) {
        return entityTemplate
                .select(R2dbcEBookSegment.class)
                .matching(query(
                        where("book_id")
                                .is(UUID.fromString(ebookId)))
                        .offset(skip).limit(limit))
                .all();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Mono<R2dbcEBookSegment> getSegmentByBookIdAndIndex(@NonNull String ebookId, int index) {
        return entityTemplate
                .select(R2dbcEBookSegment.class)
                .matching(query(where("book_id")
                        .is(UUID.fromString(ebookId))
                        .and("index").is(index)))
                .one();
    }

    @Override
    public Mono<String> createSegment(@NonNull EBookSegment ebookSegment) {
        R2dbcEBookSegment r2dbcEBookSegment = new R2dbcEBookSegment(ebookSegment);
        return this.entityTemplate
                .insert(r2dbcEBookSegment)
                .map(R2dbcEBookSegment::getId);
    }

    @Override
    public Mono<Void> deleteSegmentsByBookId(@NonNull String ebookId) {
        return entityTemplate
                .delete(R2dbcEBookSegment.class)
                .matching(query(where("book_id").is(UUID.fromString(ebookId))))
                .all()
                .doOnNext(deleteCount -> log.info("{} segments deleted", deleteCount))
                .then();
    }
}
