package com.github.sujankumarmitra.libraryservice.v1.service.impl;

import com.github.sujankumarmitra.libraryservice.v1.dao.RejectedLeaseDao;
import com.github.sujankumarmitra.libraryservice.v1.model.RejectedLease;
import com.github.sujankumarmitra.libraryservice.v1.service.RejectedLeaseRequestService;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * @author skmitra
 * @since Dec 06/12/21, 2021
 */
@Service
@AllArgsConstructor
public class DefaultRejectedLeaseRequestService implements RejectedLeaseRequestService {

    @NonNull
    private final RejectedLeaseDao rejectedLeaseDao;

    @Override
    public Mono<RejectedLease> getByLeaseRequestId(String leaseRequestId) {
        return rejectedLeaseDao
                .getRejectedLease(leaseRequestId);
    }
}
