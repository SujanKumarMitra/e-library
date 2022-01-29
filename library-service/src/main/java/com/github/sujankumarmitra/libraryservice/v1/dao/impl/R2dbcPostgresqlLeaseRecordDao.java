package com.github.sujankumarmitra.libraryservice.v1.dao.impl;

import com.github.sujankumarmitra.libraryservice.v1.dao.LeaseRecordDao;
import com.github.sujankumarmitra.libraryservice.v1.dao.impl.entity.R2dbcLeaseRecord;
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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static com.github.sujankumarmitra.libraryservice.v1.model.AcceptedLease.INFINITE_LEASE_DURATION;
import static com.github.sujankumarmitra.libraryservice.v1.model.LeaseStatus.ACCEPTED;
import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;

/**
 * @author skmitra
 * @since Dec 07/12/21, 2021
 */
@Repository
@AllArgsConstructor
@Slf4j
public class R2dbcPostgresqlLeaseRecordDao implements LeaseRecordDao {

    public static final String INSERT_STATEMENT = "INSERT INTO accepted_lease_requests(lease_request_id,start_time,duration,relinquished) values($1,$2,$3,$4)";
    public static final String LEASE_REQUESTS_FOREIGN_KEY_CONSTRAINT_NAME = "fk_accepted_lease_requests_lease_requests";
    public static final String ACCEPTED_LEASE_REQUESTS_PRIMARY_KEY_CONSTRAINT_NAME = "pk_accepted_lease_requests";
    public static final String SELECT_ACTIVE_RECORDS_STATEMENT = "SELECT alr.* FROM accepted_lease_requests alr " +
            "WHERE alr.relinquished = FALSE AND alr.lease_request_id IN (" +
            "SELECT DISTINCT lr.id FROM lease_requests lr WHERE lr.library_id = $1 AND lr.status = 'ACCEPTED')" +
            "OFFSET $2 LIMIT $3";

    public static final String SELECT_ACTIVE_RECORDS_BY_USER_ID_STATEMENT = "SELECT alr.* FROM accepted_lease_requests alr " +
            "WHERE alr.relinquished = FALSE AND alr.lease_request_id IN (" +
            "SELECT DISTINCT lr.id FROM lease_requests lr WHERE lr.user_id = $1 AND lr.library_id=$2 AND lr.status = 'ACCEPTED')" +
            "OFFSET $3 LIMIT $4";
    public static final String UPDATE_RELINQUISH_STATEMENT = "UPDATE accepted_lease_requests SET relinquished=TRUE WHERE lease_request_id=$1";
    public static final String SELECT_STALE_E_BOOK_LEASE_REQUEST_STATEMENT = "SELECT alr.lease_request_id FROM accepted_lease_requests alr " +
            "WHERE alr.relinquished = FALSE AND alr.duration != $1 AND alr.start_time + alr.duration < $2 AND alr.lease_request_id IN " +
            "(SELECT DISTINCT lr.id FROM lease_requests lr WHERE lr.status=$3 AND lr.book_id IN " +
            "(SELECT book_id FROM ebooks eb WHERE eb.book_id=lr.book_id))";

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
                    .bind("$2", leaseRecord.getStartTimeInEpochMilliseconds())
                    .bind("$3", leaseRecord.getDurationInMilliseconds())
                    .bind("$4", leaseRecord.isRelinquished())
                    .fetch()
                    .rowsUpdated()
                    .doOnSuccess(rowsUpdated -> log.debug("Created lease record " + leaseRecord))
                    .then()
                    .onErrorMap(DataIntegrityViolationException.class, err -> translateError(err, leaseRequestId));
        });
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<LeaseRecord> getLeaseRecord(String leaseRequestId) {
        return Mono.defer(() -> {
            UUID uuid;
            try {
                uuid = UUID.fromString(leaseRequestId);
            } catch (Exception e) {
                log.debug("{} is not valid uuid, returning empty Mono", leaseRequestId);
                return Mono.empty();
            }

            return this.entityTemplate
                    .select(R2dbcLeaseRecord.class)
                    .matching(query(where("lease_request_id").is(uuid)))
                    .one()
                    .cast(LeaseRecord.class);
        });
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<LeaseRecord> getActiveLeaseRecords(String libraryId, int skip, int limit) {
        return this.entityTemplate
                .getDatabaseClient()
                .sql(SELECT_ACTIVE_RECORDS_STATEMENT)
                .bind("$1", libraryId)
                .bind("$2", skip)
                .bind("$3", limit)
                .map(row -> entityTemplate.getConverter().read(R2dbcLeaseRecord.class, row))
                .all()
                .cast(LeaseRecord.class);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<LeaseRecord> getActiveLeaseRecords(String libraryId, String userId, int skip, int limit) {
        return this.entityTemplate
                .getDatabaseClient()
                .sql(SELECT_ACTIVE_RECORDS_BY_USER_ID_STATEMENT)
                .bind("$1", userId)
                .bind("$2", libraryId)
                .bind("$3", skip)
                .bind("$4", limit)
                .map(row -> entityTemplate.getConverter().read(R2dbcLeaseRecord.class, row))
                .all()
                .cast(LeaseRecord.class);
    }

    @Override
    @Transactional
    public Mono<Void> markAsRelinquished(String leaseRequestId) {
        return Mono.defer(() -> {

            UUID uuid;
            try {
                uuid = UUID.fromString(leaseRequestId);
            } catch (Exception e) {
                log.debug("{} is not a valid uuid, returning empty Mono", leaseRequestId);
                return Mono.empty();
            }

            return entityTemplate
                    .getDatabaseClient()
                    .sql(UPDATE_RELINQUISH_STATEMENT)
                    .bind("$1", uuid)
                    .fetch()
                    .rowsUpdated()
                    .then();
        });
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<String> getStaleEBookLeaseRecordIds() {
        Long currentTime = System.currentTimeMillis();
        return entityTemplate
                .getDatabaseClient()
                .sql(SELECT_STALE_E_BOOK_LEASE_REQUEST_STATEMENT)
                .bind("$1", INFINITE_LEASE_DURATION)
                .bind("$2", currentTime)
                .bind("$3", ACCEPTED.toString())
                .map(row -> row.get("lease_request_id", UUID.class))
                .all()
                .map(Object::toString);
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
