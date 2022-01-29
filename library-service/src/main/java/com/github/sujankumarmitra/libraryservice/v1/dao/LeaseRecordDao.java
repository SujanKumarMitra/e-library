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

    Mono<LeaseRecord> getLeaseRecord(String leaseRequestId);

    Flux<LeaseRecord> getActiveLeaseRecords(String libraryId, int skip, int limit);

    Flux<LeaseRecord> getActiveLeaseRecords(String libraryId, String userId, int skip, int limit);

    Mono<Void> markAsRelinquished(String leaseRequestId);

    Flux<String> getStaleEBookLeaseRecordIds();
}
