package com.github.sujankumarmitra.libraryservice.v1.dao;

import com.github.sujankumarmitra.libraryservice.v1.model.LeaseRecord;
import reactor.core.publisher.Mono;

/**
 * @author skmitra
 * @since Dec 07/12/21, 2021
 */
public interface LeaseRecordDao {
    Mono<Void> createLeaseRecord(LeaseRecord leaseRecord);
}
