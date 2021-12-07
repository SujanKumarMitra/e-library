package com.github.sujankumarmitra.libraryservice.v1.dao.impl;

import com.github.sujankumarmitra.libraryservice.v1.dao.LeaseRequestDao;
import com.github.sujankumarmitra.libraryservice.v1.model.LeaseRequest;
import com.github.sujankumarmitra.libraryservice.v1.model.LeaseStatus;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author skmitra
 * @since Dec 07/12/21, 2021
 */
@Repository
public class R2dbcPostgresqlLeaseRequestDao implements LeaseRequestDao {
    @Override
    public <R extends LeaseRequest> Mono<R> getLeaseRequest(String leaseRequestId) {
        return null;
    }

    @Override
    public <R extends LeaseRequest> Flux<R> getPendingLeaseRequests(int skip, int limit) {
        return null;
    }

    @Override
    public <R extends LeaseRequest> Flux<R> getPendingLeaseRequests(String userId, int skip, int limit) {
        return null;
    }

    @Override
    public Mono<String> createLeaseRequest(LeaseRequest request) {
        return null;
    }

    @Override
    public Mono<Void> deleteLeaseRequest(String leaseRequestId) {
        return null;
    }

    @Override
    public Mono<Void> setLeaseStatus(String leaseRequestId, LeaseStatus status) {
        return null;
    }
}
