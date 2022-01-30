package com.github.sujankumarmitra.libraryservice.v1.dao;

import com.github.sujankumarmitra.libraryservice.v1.model.RejectedLease;
import reactor.core.publisher.Mono;

/**
 * @author skmitra
 * @since Dec 06/12/21, 2021
 */
public interface RejectedLeaseDao {

    Mono<RejectedLease> getRejectedLease(String leaseRequestId);

    Mono<Void> createRejectedLease(RejectedLease rejectedLease);

}
