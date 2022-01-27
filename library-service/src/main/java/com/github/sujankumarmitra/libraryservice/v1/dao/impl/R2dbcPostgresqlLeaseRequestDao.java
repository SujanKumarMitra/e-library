package com.github.sujankumarmitra.libraryservice.v1.dao.impl;

import com.github.sujankumarmitra.libraryservice.v1.dao.LeaseRequestDao;
import com.github.sujankumarmitra.libraryservice.v1.dao.impl.entity.R2dbcLeaseRequest;
import com.github.sujankumarmitra.libraryservice.v1.exception.BookNotFoundException;
import com.github.sujankumarmitra.libraryservice.v1.model.LeaseRequest;
import com.github.sujankumarmitra.libraryservice.v1.model.LeaseStatus;
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

import static com.github.sujankumarmitra.libraryservice.v1.model.LeaseStatus.PENDING;
import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;
import static org.springframework.data.relational.core.query.Update.update;

/**
 * @author skmitra
 * @since Dec 07/12/21, 2021
 */
@Repository
@Slf4j
@AllArgsConstructor
public class R2dbcPostgresqlLeaseRequestDao implements LeaseRequestDao {

    public static final String STATUS_COLUMN_NAME = "status";
    public static final String BOOKS_FOREIGN_KEY_CONSTRAINT_NAME = "fk_lease_requests_books";
    @NonNull
    private final R2dbcEntityTemplate entityTemplate;

    @Override
    @Transactional(readOnly = true)
    public Mono<LeaseRequest> getLeaseRequest(@NonNull String leaseRequestId) {
        return Mono.defer(() -> {
            UUID uuid;
            try {
                uuid = UUID.fromString(leaseRequestId);
            } catch (IllegalArgumentException e) {
                log.info("{} is not valid uuid, returning empty Mono", leaseRequestId);
                return Mono.empty();
            }

            return entityTemplate
                    .select(R2dbcLeaseRequest.class)
                    .matching(query(where("id").is(uuid)))
                    .first()
                    .cast(LeaseRequest.class);
        });
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<LeaseRequest> getPendingLeaseRequests(@NonNull String libraryId, int skip, int limit) {
        return entityTemplate
                .select(R2dbcLeaseRequest.class)
                .matching(query(where(STATUS_COLUMN_NAME).is(PENDING.toString())
                        .and(where("library_id").is(libraryId)))
                        .offset(skip)
                        .limit(limit))
                .all()
                .cast(LeaseRequest.class);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<LeaseRequest> getPendingLeaseRequests(@NonNull String libraryId, @NonNull String userId, int skip, int limit) {
        return entityTemplate
                .select(R2dbcLeaseRequest.class)
                .matching(query(where(STATUS_COLUMN_NAME).is(PENDING.toString())
                        .and(where("user_id").is(userId))
                        .and(where("library_id").is(libraryId)))
                        .offset(skip)
                        .limit(limit))
                .all()
                .cast(LeaseRequest.class);
    }

    @Override
    @Transactional
    public Mono<String> createLeaseRequest(@NonNull LeaseRequest leaseRequest) {
        return Mono.defer(() -> {
            R2dbcLeaseRequest r2dbcLeaseRequest;

            try {
                r2dbcLeaseRequest = new R2dbcLeaseRequest(leaseRequest);
            } catch (IllegalArgumentException ex) {
                String bookId = leaseRequest.getBookId();
                log.debug("{} is not a valid uuid for book, returning Mono.error(BookNotFoundException)", bookId);
                return Mono.error(new BookNotFoundException(bookId));
            }

            return entityTemplate
                    .insert(r2dbcLeaseRequest)
                    .map(R2dbcLeaseRequest::getId)
                    .onErrorMap(DataIntegrityViolationException.class, err -> translateErrors(err, leaseRequest));

        });
    }

    private Throwable translateErrors(DataIntegrityViolationException ex, LeaseRequest leaseRequest) {
        log.debug("DB integrity error", ex);
        String message = ex.getMessage();

        if (message == null) {
            log.debug("DataIntegrityViolationException.getMessage() returned null, exception translation not possible, falling back to original exception");
            return ex;
        }

        if (message.contains(BOOKS_FOREIGN_KEY_CONSTRAINT_NAME)) {
            return new BookNotFoundException(leaseRequest.getBookId());
        }

        log.debug("failed to translate error, falling back to original thrown ex");
        return ex;
    }

    @Override
    @Transactional
    public Mono<Void> deletePendingLeaseRequest(@NonNull String leaseRequestId) {
        return Mono.defer(() -> {
            UUID uuid;
            try {
                uuid = UUID.fromString(leaseRequestId);
            } catch (IllegalArgumentException ex) {
                log.debug("{} is not valid uuid, returning empty Mono", leaseRequestId);
                return Mono.empty();
            }

            return entityTemplate
                    .delete(R2dbcLeaseRequest.class)
                    .matching(query(where("id").is(uuid)
                            .and(where(STATUS_COLUMN_NAME).is(PENDING.toString()))))
                    .all()
                    .doOnNext(deleteCount -> {
                        if (deleteCount > 0) log.debug("deleted lease request with id {} ", leaseRequestId);
                        else log.debug("no lease request found with id {}", leaseRequestId);
                    })
                    .then();
        });
    }

    @Override
    @Transactional
    public Mono<Void> setLeaseStatus(@NonNull String leaseRequestId, @NonNull LeaseStatus status) {
        return Mono.defer(() -> {
            UUID uuid;
            try {
                uuid = UUID.fromString(leaseRequestId);
            } catch (IllegalArgumentException ex) {
                log.info("{} is not a valid uuid, returning empty Mono", leaseRequestId);
                return Mono.empty();
            }

            return entityTemplate
                    .update(R2dbcLeaseRequest.class)
                    .matching(query(where("id").is(uuid)))
                    .apply(update(STATUS_COLUMN_NAME, status.toString()))
                    .doOnNext(updateCount -> {
                        if (updateCount > 0) log.debug("lease status with id {} updated to {}", leaseRequestId, status);
                        else log.debug("no lease request found with id {}", leaseRequestId);
                    })
                    .then();
        });
    }
}
