package com.github.sujankumarmitra.libraryservice.v1.service;

import com.github.sujankumarmitra.libraryservice.v1.model.LeaseRecord;
import com.github.sujankumarmitra.libraryservice.v1.model.Money;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author skmitra
 * @since Dec 08/12/21, 2021
 */
public interface ActiveLeaseService {
    Flux<LeaseRecord> getAllActiveLeases(String libraryId, int pageNo);

    Flux<LeaseRecord> getAllActiveLeases(String libraryId, String userId, int pageNo);

    Mono<Money> getFineForActiveLease(String leaseRequestId);

    Mono<Void> relinquishActiveLease(String leaseRequestId);

    Mono<Void> invalidateStaleEBookLeases();
}
