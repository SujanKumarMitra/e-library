package com.github.sujankumarmitra.libraryservice.v1.dao.impl;

import com.github.sujankumarmitra.libraryservice.v1.dao.EBookSegmentDao;
import com.github.sujankumarmitra.libraryservice.v1.dao.impl.entity.R2dbcEBookSegment;
import com.github.sujankumarmitra.libraryservice.v1.exception.BookNotFoundException;
import com.github.sujankumarmitra.libraryservice.v1.exception.DuplicateEBookSegmentIndexException;
import com.github.sujankumarmitra.libraryservice.v1.model.EBookSegment;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static java.util.UUID.fromString;
import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;

/**
 * @author skmitra
 * @since Dec 10/12/21, 2021
 */
@Repository
@Slf4j
@AllArgsConstructor
public class R2dbcPostgresqlEBookSegmentDao implements EBookSegmentDao {
    public static final String EBOOKS_FOREIGN_KEY_CONSTRAINT_NAME = "fk_ebook_segments_books";
    public static final String BOOK_ID_COLUMN_NAME = "book_id";
    public static final String UNIQUE_SEGMENT_INDEX_CONSTRAINT_NAME = "unq_ebook_segments_book_id_index";
    @NonNull
    private final R2dbcEntityTemplate entityTemplate;

    @Override
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public Flux<R2dbcEBookSegment> getSegmentsByBookId(@NonNull String ebookId, int skip, int limit) {
        return Flux.defer(() -> {
            UUID uuid;
            try {
                uuid = fromString(ebookId);
            } catch (Exception e) {
                log.debug("{} is not a valid uuid, returning empty Flux", ebookId);
                return Flux.empty();
            }
            return entityTemplate
                    .select(R2dbcEBookSegment.class)
                    .matching(query(where(BOOK_ID_COLUMN_NAME).is(uuid))
                            .offset(skip).limit(limit))
                    .all();
        });
    }

    @Override
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public Mono<R2dbcEBookSegment> getSegmentByBookIdAndIndex(@NonNull String ebookId, int index) {
        return Mono.defer(() -> {
            UUID uuid;
            try {
                uuid = fromString(ebookId);
            } catch (Exception e) {
                log.debug("{} is not a valid uuid, returning empty Mono", ebookId);
                return Mono.empty();
            }
            return entityTemplate
                    .select(R2dbcEBookSegment.class)
                    .matching(query(where(BOOK_ID_COLUMN_NAME)
                            .is(uuid)
                            .and("index").is(index)))
                    .one();
        });
    }

    @Override
    @Transactional
    public Mono<String> createSegment(@NonNull EBookSegment ebookSegment) {
        return Mono.defer(() -> {
            R2dbcEBookSegment r2dbcEBookSegment;
            try {
                r2dbcEBookSegment = new R2dbcEBookSegment(ebookSegment);
            } catch (IllegalArgumentException ex) {
                log.debug("Error", ex);
                return Mono.error(new BookNotFoundException(ebookSegment.getBookId()));
            }
            return this.entityTemplate
                    .insert(r2dbcEBookSegment)
                    .map(R2dbcEBookSegment::getId)
                    .onErrorMap(DataIntegrityViolationException.class, err -> translateErrors(err, ebookSegment));
        });
    }

    private Throwable translateErrors(DataIntegrityViolationException ex, EBookSegment ebookSegment) {
        log.debug("DB integrity error", ex);
        String message = ex.getMessage();

        if (message == null) {
            log.debug("DataIntegrityViolationException.getMessage() returned null, translation not possible, falling back to original ex");
            return ex;
        }

        if (message.contains(EBOOKS_FOREIGN_KEY_CONSTRAINT_NAME)) {
            return new BookNotFoundException(ebookSegment.getBookId());
        }

        if(message.contains(UNIQUE_SEGMENT_INDEX_CONSTRAINT_NAME)) {
            return new DuplicateEBookSegmentIndexException(ebookSegment.getBookId(), ebookSegment.getIndex());
        }

        log.debug("failed to translate error message, falling back to original exception");
        return ex;
    }

    @Override
    @Transactional
    public Mono<Void> deleteSegmentsByBookId(@NonNull String ebookId) {
        return Mono.defer(() -> {
            UUID uuid;
            try {
                uuid = fromString(ebookId);
            } catch (Exception e) {
                log.debug("{} is not a valid uuid, returning empty Mono", ebookId);
                return Mono.empty();
            }
            return entityTemplate
                    .delete(R2dbcEBookSegment.class)
                    .matching(query(where(BOOK_ID_COLUMN_NAME).is(uuid)))
                    .all()
                    .doOnNext(deleteCount -> log.info("{} segments deleted", deleteCount))
                    .then();
        });
    }
}
