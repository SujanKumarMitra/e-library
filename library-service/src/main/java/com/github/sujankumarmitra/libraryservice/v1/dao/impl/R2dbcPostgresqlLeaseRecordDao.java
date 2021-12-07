package com.github.sujankumarmitra.libraryservice.v1.dao.impl;

import com.github.sujankumarmitra.libraryservice.v1.dao.LeaseRecordDao;
import com.github.sujankumarmitra.libraryservice.v1.model.LeaseRecord;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

/**
 * @author skmitra
 * @since Dec 07/12/21, 2021
 */
@Repository
public class R2dbcPostgresqlLeaseRecordDao implements LeaseRecordDao {
    @Override
    public Mono<Void> createLeaseRecord(LeaseRecord leaseRecord) {
        return null;
    }
}
