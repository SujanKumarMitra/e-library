package com.github.sujankumarmitra.libraryservice.v1.service.impl;

import com.github.sujankumarmitra.libraryservice.v1.dao.LeaseRequestDao;
import com.github.sujankumarmitra.libraryservice.v1.model.LeaseRequest;
import com.github.sujankumarmitra.libraryservice.v1.model.RejectedLease;
import com.github.sujankumarmitra.libraryservice.v1.service.RejectedLeaseRequestService;
import com.github.sujankumarmitra.libraryservice.v1.util.SecurityUtil;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.context.annotation.Primary;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.github.sujankumarmitra.libraryservice.v1.security.SecurityConstants.ROLE_STUDENT;
import static com.github.sujankumarmitra.libraryservice.v1.security.SecurityConstants.ROLE_TEACHER;

/**
 * @author skmitra
 * @since Jan 29/01/22, 2022
 */
@Service
@Primary
@AllArgsConstructor
public class SecuredRejectedLeaseRequestService implements RejectedLeaseRequestService {
    @NonNull
    private final RejectedLeaseRequestService delegate;
    @NonNull
    private final LeaseRequestDao leaseRequestDao;

    @Override
    public Mono<RejectedLease> getByLeaseRequestId(String leaseRequestId) {
        return leaseRequestDao
                .getLeaseRequest(leaseRequestId)
                .filterWhen(this::currentUserMatchesWithUserId)
                .map(LeaseRequest::getLibraryId)
                .filterWhen(this::currentUserHasAccessToLibrary)
                .switchIfEmpty(Mono.error(() -> new AccessDeniedException("Denied")))
                .then(delegate.getByLeaseRequestId(leaseRequestId));
    }

    private Flux<Boolean> currentUserHasAccessToLibrary(String libraryId) {
        return Flux.just(ROLE_TEACHER, ROLE_STUDENT)
                .map(role -> libraryId + ":" + role)
                .flatMap(SecurityUtil::hasAuthority)
                .filter(Boolean::booleanValue);
    }

    private Mono<Boolean> currentUserMatchesWithUserId(LeaseRequest leaseRequest) {
        return SecurityUtil.getCurrentUser()
                .map(Authentication::getName)
                .map(username -> username.equals(leaseRequest.getUserId()));
    }
}
