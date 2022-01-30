package com.github.sujankumarmitra.libraryservice.v1.service;

import com.github.sujankumarmitra.libraryservice.v1.model.AcceptedLease;
import com.github.sujankumarmitra.libraryservice.v1.model.LeaseRequest;
import com.github.sujankumarmitra.libraryservice.v1.model.RejectedLease;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author skmitra
 * @since Dec 06/12/21, 2021
 */
public interface LeaseRequestService {
    Flux<LeaseRequest> getPendingLeaseRequests(String libraryId, int pageNo);

    Flux<LeaseRequest> getPendingLeaseRequests(String libraryId, String userId, int pageNo);

    Mono<Void> cancelLeaseRequest(String leaseRequestId);

    Mono<String> createLeaseRequest(LeaseRequest request);

    Mono<Void> acceptLeaseRequest(AcceptedLease acceptedLease);

    Mono<Void> rejectLeaseRequest(RejectedLease rejectedLease);
}
