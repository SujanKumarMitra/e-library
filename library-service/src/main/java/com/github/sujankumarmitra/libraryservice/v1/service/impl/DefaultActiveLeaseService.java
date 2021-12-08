package com.github.sujankumarmitra.libraryservice.v1.service.impl;

import com.github.sujankumarmitra.libraryservice.v1.model.LeaseRecord;
import com.github.sujankumarmitra.libraryservice.v1.model.Money;
import com.github.sujankumarmitra.libraryservice.v1.service.ActiveLeaseService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author skmitra
 * @since Dec 08/12/21, 2021
 */
@Service
public class DefaultActiveLeaseService implements ActiveLeaseService {
    @Override
    public Flux<LeaseRecord> getAllActiveLeases(long pageNo) {
        return null;
    }

    @Override
    public Flux<LeaseRecord> getAllActiveLeases(String userId, long pageNo) {
        return null;
    }

    @Override
    public Mono<Money> getFineForActiveLease(String leaseRequestId) {
        return null;
    }

    @Override
    public Mono<Void> relinquishActiveLease(String leaseRequestId) {
        return null;
    }

    @Override
    public Mono<Void> invalidateStateEBookLeases() {
        return null;
    }
}
