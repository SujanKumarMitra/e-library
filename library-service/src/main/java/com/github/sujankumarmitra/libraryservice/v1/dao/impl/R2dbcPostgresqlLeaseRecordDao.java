package com.github.sujankumarmitra.libraryservice.v1.dao.impl;

import com.github.sujankumarmitra.libraryservice.v1.dao.LeaseRecordDao;
import com.github.sujankumarmitra.libraryservice.v1.exception.LeaseRecordAlreadyExistsException;
import com.github.sujankumarmitra.libraryservice.v1.exception.LeaseRequestNotFoundException;
import com.github.sujankumarmitra.libraryservice.v1.model.LeaseRecord;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * @author skmitra
 * @since Dec 07/12/21, 2021
 */
@Repository
@AllArgsConstructor
@Slf4j
public class R2dbcPostgresqlLeaseRecordDao implements LeaseRecordDao {

    public static final String INSERT_STATEMENT = "INSERT INTO accepted_lease_requests(lease_request_id,start_time,end_time,relinquished) values($1,$2,$3,$4)";
    public static final String LEASE_REQUESTS_FOREIGN_KEY_CONSTRAINT_NAME = "fk_accepted_lease_requests_lease_requests";
    public static final String ACCEPTED_LEASE_REQUESTS_PRIMARY_KEY_CONSTRAINT_NAME = "pk_accepted_lease_requests";

    @NonNull
    private final R2dbcEntityTemplate entityTemplate;

    @Override
    @Transactional
    public Mono<Void> createLeaseRecord(@NonNull LeaseRecord leaseRecord) {
        return Mono.defer(() -> {
            String leaseRequestId = leaseRecord.getLeaseRequestId();

            UUID uuid;
            try {
                uuid = UUID.fromString(leaseRequestId);
            } catch (IllegalArgumentException | NullPointerException ex) {
                log.debug("{} is not valid uuid, returning Mono.error(LeaseRequestNodeFoundException)", leaseRequestId);
                return Mono.error(new LeaseRequestNotFoundException(leaseRequestId));
            }

            return entityTemplate
                    .getDatabaseClient()
                    .sql(INSERT_STATEMENT)
                    .bind("$1", uuid)
                    .bind("$2", leaseRecord.getStartTime())
                    .bind("$3", leaseRecord.getEndTime())
                    .bind("$4", leaseRecord.isRelinquished())
                    .fetch()
                    .rowsUpdated()
                    .doOnSuccess(rowsUpdated -> log.debug("Created lease record " + leaseRecord))
                    .then()
                    .onErrorMap(DataIntegrityViolationException.class, err -> translateError(err, leaseRequestId));
        });
    }

    private Throwable translateError(DataIntegrityViolationException ex, String leaseRequestId) {
        log.debug("DB integrity error", ex);

        String message = ex.getMessage();

        if (message == null) {
            log.debug("DataIntegrityViolationException.getMessage() is null, cannot translate exception, falling back to original exception");
            return ex;
        }

        if (message.contains(LEASE_REQUESTS_FOREIGN_KEY_CONSTRAINT_NAME)) {
            return new LeaseRequestNotFoundException(leaseRequestId);
        }

        if (message.contains(ACCEPTED_LEASE_REQUESTS_PRIMARY_KEY_CONSTRAINT_NAME)) {
            return new LeaseRecordAlreadyExistsException(leaseRequestId);
        }

        log.debug("failed to translate error, falling back to original exception");
        return ex;
    }
}
