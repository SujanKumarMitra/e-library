package com.github.sujankumarmitra.libraryservice.v1.dao;

import com.github.sujankumarmitra.libraryservice.v1.model.AcceptedLease;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author skmitra
 * @since Dec 07/12/21, 2021
 */
public interface AcceptedLeaseDao {
    Mono<Void> createLeaseRecord(AcceptedLease leaseRecord);

    Mono<AcceptedLease> getLeaseRecord(String leaseRequestId);

    Flux<AcceptedLease> getActiveLeaseRecords(String libraryId, int skip, int limit);

    Flux<AcceptedLease> getActiveLeaseRecords(String libraryId, String userId, int skip, int limit);

    Mono<Void> markAsRelinquished(String leaseRequestId);

    Flux<String> getStaleEBookLeaseRecordIds();
}
