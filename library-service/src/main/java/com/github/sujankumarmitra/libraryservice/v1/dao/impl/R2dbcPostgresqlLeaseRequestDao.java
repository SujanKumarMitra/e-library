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
    @SuppressWarnings("unchecked")
    public Mono<R2dbcLeaseRequest> getLeaseRequest(@NonNull String leaseRequestId) {
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
                    .first();
        });
    }

    @Override
    @SuppressWarnings("unchecked")
    public Flux<R2dbcLeaseRequest> getPendingLeaseRequests(int skip, int limit) {
        return entityTemplate
                .select(R2dbcLeaseRequest.class)
                .matching(query(where(STATUS_COLUMN_NAME).is(PENDING.toString()))
                        .offset(skip)
                        .limit(limit))
                .all();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Flux<R2dbcLeaseRequest> getPendingLeaseRequests(@NonNull String userId, int skip, int limit) {
        return entityTemplate
                .select(R2dbcLeaseRequest.class)
                .matching(query(where(STATUS_COLUMN_NAME).is(PENDING.toString())
                        .and(where("user_id").is(userId)))
                        .offset(skip)
                        .limit(limit))
                .all();
    }

    @Override
    public Mono<String> createLeaseRequest(@NonNull LeaseRequest leaseRequest) {
        return Mono.defer(() -> {
            R2dbcLeaseRequest r2dbcLeaseRequest = new R2dbcLeaseRequest();

            r2dbcLeaseRequest.setTimestamp(leaseRequest.getTimestamp());
            r2dbcLeaseRequest.setStatus(leaseRequest.getStatus());
            String bookId = leaseRequest.getBookId();
            try {
                r2dbcLeaseRequest.setBookId(UUID.fromString(bookId));
            } catch (IllegalArgumentException ex) {
                log.debug("{} is not a valid uuid for book, returning Mono.error(BookNotFoundException)", bookId);
                return Mono.error(new BookNotFoundException(bookId));
            }
            r2dbcLeaseRequest.setUserId(leaseRequest.getUserId());

            return entityTemplate
                    .insert(r2dbcLeaseRequest)
                    .map(R2dbcLeaseRequest::getId)
                    .onErrorMap(DataIntegrityViolationException.class, err -> translateErrors(err, leaseRequest));

        });
    }

    private Throwable translateErrors(DataIntegrityViolationException ex, LeaseRequest leaseRequest) {
        log.debug("DB integrity error", ex);
        String message = ex.getMessage();

        if(message == null) {
            log.debug("DataIntegrityViolationException.getMessage() returned null, exception translation not possible, falling back to original exception");
            return ex;
        }

        if(message.contains(BOOKS_FOREIGN_KEY_CONSTRAINT_NAME)) {
            return new BookNotFoundException(leaseRequest.getBookId());
        }

        log.debug("failed to translate error, falling back to original thrown ex");
        return ex;
    }

    @Override
    public Mono<Void> deleteLeaseRequest(@NonNull String leaseRequestId) {
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
                    .matching(query(where("id").is(uuid)))
                    .all()
                    .doOnNext(deleteCount -> {
                        if (deleteCount > 0) log.debug("deleted lease request with id {} ", leaseRequestId);
                        else log.debug("no lease request found with id {}", leaseRequestId);
                    })
                    .then();
        });
    }

    @Override
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
