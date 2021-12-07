package com.github.sujankumarmitra.libraryservice.v1.dao;

import com.github.sujankumarmitra.libraryservice.v1.model.LeaseRequest;
import com.github.sujankumarmitra.libraryservice.v1.model.LeaseStatus;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author skmitra
 * @since Dec 07/12/21, 2021
 */
public interface LeaseRequestDao {

    <R extends LeaseRequest> Mono<R> getLeaseRequest(String leaseRequestId);

    <R extends LeaseRequest> Flux<R> getPendingLeaseRequests(int skip, int limit);

    <R extends LeaseRequest> Flux<R> getPendingLeaseRequests(String userId, int skip, int limit);

    Mono<String> createLeaseRequest(LeaseRequest request);

    Mono<Void> deleteLeaseRequest(String leaseRequestId);

    Mono<Void> setLeaseStatus(String leaseRequestId, LeaseStatus status);
}
