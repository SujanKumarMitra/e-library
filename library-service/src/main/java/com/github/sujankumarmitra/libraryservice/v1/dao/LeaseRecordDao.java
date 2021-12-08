package com.github.sujankumarmitra.libraryservice.v1.dao;

import com.github.sujankumarmitra.libraryservice.v1.model.LeaseRecord;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author skmitra
 * @since Dec 07/12/21, 2021
 */
public interface LeaseRecordDao {
    Mono<Void> createLeaseRecord(LeaseRecord leaseRecord);

    <R extends LeaseRecord> Mono<R> getLeaseRecord(String leaseRequestId);

    <R extends LeaseRecord>Flux<R> getActiveLeaseRecords(int skip, int limit);

    <R extends LeaseRecord> Flux<R> getActiveLeaseRecordsByUserId(String userId, int skip, int limit);

    Mono<Void> markAsRelinquished(String leaseRequestId);

    Flux<String> getStaleEBookLeaseRecordIds();
}
