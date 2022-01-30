package com.github.sujankumarmitra.libraryservice.v1.dao.impl;

import com.github.sujankumarmitra.libraryservice.v1.dao.RejectedLeaseDao;
import com.github.sujankumarmitra.libraryservice.v1.dao.impl.entity.R2dbcRejectedLease;
import com.github.sujankumarmitra.libraryservice.v1.exception.LeaseRequestNotFoundException;
import com.github.sujankumarmitra.libraryservice.v1.model.RejectedLease;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;

/**
 * @author skmitra
 * @since Dec 06/12/21, 2021
 */
@Repository
@AllArgsConstructor
@Slf4j
public class R2dbcPostgresqlRejectedLeaseDao implements RejectedLeaseDao {

    public static final String INSERT_STATEMENT = "INSERT INTO rejected_lease_requests(lease_request_id,reason_phrase) VALUES($1,$2)";
    public static final String LEASE_REQUESTS_FOREIGN_KEY_CONSTRAINT_NAME = "fk_rejected_lease_requests_lease_requests";
    @NonNull
    private final R2dbcEntityTemplate entityTemplate;

    @Override
    public Mono<RejectedLease> getRejectedLease(@NonNull String leaseRequestId) {
        return Mono.defer(() -> {
            UUID id;
            try {
                id = UUID.fromString(leaseRequestId);
            } catch (IllegalArgumentException e) {
                log.info("{} is not valid uuid, returning empty mono", leaseRequestId);
                return Mono.empty();
            }

            return entityTemplate
                    .select(R2dbcRejectedLease.class)
                    .matching(query(where("lease_request_id").is(id)))
                    .first()
                    .cast(RejectedLease.class);
        });
    }


    @Override
    public Mono<Void> createRejectedLease(@NonNull RejectedLease rejectedLease) {
        return Mono.defer(() -> {
            R2dbcRejectedLease r2dbcRejectedLease;
            try {
                r2dbcRejectedLease = new R2dbcRejectedLease(rejectedLease);
            } catch (IllegalArgumentException e) {
                log.debug("{} is not valid uuid, returning Mono.error(LeaseRequestNotFoundException)", rejectedLease.getLeaseRequestId());
                return Mono.error(new LeaseRequestNotFoundException(rejectedLease.getLeaseRequestId()));
            }

            return entityTemplate
                    .getDatabaseClient()
                    .sql(INSERT_STATEMENT)
                    .bind("$1", r2dbcRejectedLease.getLeaseRequestUuid())
                    .bind("$2", r2dbcRejectedLease.getReasonPhrase())
                    .fetch()
                    .rowsUpdated()
                    .doOnSuccess(v -> log.debug("created rejected lease"))
                    .then()
                    .onErrorMap(DataIntegrityViolationException.class, err -> translateError(err, rejectedLease));
        });
    }

    private Throwable translateError(DataIntegrityViolationException ex, RejectedLease r2dbcRejectedLease) {
        log.debug("DB integrity error", ex);
        String message = ex.getMessage();
        if (message != null && message.contains(LEASE_REQUESTS_FOREIGN_KEY_CONSTRAINT_NAME)) {
            return new LeaseRequestNotFoundException(r2dbcRejectedLease.getLeaseRequestId());
        }

        log.debug("failed to translate error, falling back to originally thrown exception");
        return ex;
    }
}
