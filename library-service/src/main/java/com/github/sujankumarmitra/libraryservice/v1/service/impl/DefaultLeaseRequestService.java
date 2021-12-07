package com.github.sujankumarmitra.libraryservice.v1.service.impl;

import com.github.sujankumarmitra.libraryservice.v1.model.AcceptedLease;
import com.github.sujankumarmitra.libraryservice.v1.model.LeaseRequest;
import com.github.sujankumarmitra.libraryservice.v1.model.RejectedLease;
import com.github.sujankumarmitra.libraryservice.v1.service.LeaseRequestService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * @author skmitra
 * @since Dec 07/12/21, 2021
 */
@Service
public class DefaultLeaseRequestService implements LeaseRequestService {
    @Override
    public Flux<LeaseRequest> getPendingLeaseRequests(int pageNo) {
        return Flux.empty();
    }

    @Override
    public Flux<LeaseRequest> getPendingLeaseRequests(String userId, int pageNo) {
        return Flux.empty();
    }

    @Override
    public Mono<Void> deleteLeaseRequest(String id) {
        return Mono.empty();
    }

    @Override
    public Mono<String> createLeaseRequest(LeaseRequest request) {
        return Mono.fromSupplier(UUID.randomUUID()::toString);
    }

    @Override
    public Mono<Void> acceptLeaseRequest(AcceptedLease acceptedLease) {
        return Mono.empty();
    }

    @Override
    public Mono<Void> rejectLeaseRequest(RejectedLease rejectedLease) {
        return Mono.empty();
    }
}
