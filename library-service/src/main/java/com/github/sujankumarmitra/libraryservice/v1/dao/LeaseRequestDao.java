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

    Mono<LeaseRequest> getLeaseRequest(String leaseRequestId);

    Flux<LeaseRequest> getPendingLeaseRequests(String libraryId, int skip, int limit);

    Flux<LeaseRequest> getPendingLeaseRequests(String libraryId, String userId, int skip, int limit);

    Mono<String> createLeaseRequest(LeaseRequest request);

    Mono<Void> deletePendingLeaseRequest(String leaseRequestId);

    Mono<Void> setLeaseStatus(String leaseRequestId, LeaseStatus status);
}
