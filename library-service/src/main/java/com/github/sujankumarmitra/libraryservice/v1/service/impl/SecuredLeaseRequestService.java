package com.github.sujankumarmitra.libraryservice.v1.service.impl;

import com.github.sujankumarmitra.libraryservice.v1.dao.LeaseRequestDao;
import com.github.sujankumarmitra.libraryservice.v1.model.AcceptedLease;
import com.github.sujankumarmitra.libraryservice.v1.model.LeaseRequest;
import com.github.sujankumarmitra.libraryservice.v1.model.RejectedLease;
import com.github.sujankumarmitra.libraryservice.v1.service.LeaseRequestService;
import com.github.sujankumarmitra.libraryservice.v1.util.SecurityUtil;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.context.annotation.Primary;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.github.sujankumarmitra.libraryservice.v1.security.SecurityConstants.*;

/**
 * @author skmitra
 * @since Jan 27/01/22, 2022
 */
@Service
@Primary
@AllArgsConstructor
public class SecuredLeaseRequestService implements LeaseRequestService {
    @NonNull
    private final LeaseRequestService delegate;
    @NonNull
    private final LeaseRequestDao leaseRequestDao;

    @Override
    @PreAuthorize("hasAuthority(#libraryId + ':" + ROLE_LIBRARIAN + "')")
    public Flux<LeaseRequest> getPendingLeaseRequests(String libraryId, int pageNo) {
        return delegate.getPendingLeaseRequests(libraryId, pageNo);
    }

    @Override
    @PreAuthorize("(authentication.name==#userId && hasAuthority(#libraryId + ':" + ROLE_STUDENT + "')) || " +
            "hasAuthority(#libraryId + ':" + ROLE_LIBRARIAN + "')")
    public Flux<LeaseRequest> getPendingLeaseRequests(String libraryId, String userId, int pageNo) {
        return delegate.getPendingLeaseRequests(libraryId, userId, pageNo);
    }

    @Override
    public Mono<Void> cancelLeaseRequest(String leaseRequestId) {
        return leaseRequestDao
                .getLeaseRequest(leaseRequestId)
                .flatMap(leaseRequest -> SecurityUtil.getCurrentUser()
                        .filter(auth -> auth.getName().equals(leaseRequest.getUserId()))
                        .filter(auth -> SecurityUtil.hasAnyAuthority(auth,
                                leaseRequest.getLibraryId() + ":" + ROLE_STUDENT,
                                leaseRequest.getLibraryId() + ":" + ROLE_TEACHER))
                        .switchIfEmpty(Mono.error(() -> new AccessDeniedException("Denied")))
                        .then(delegate.cancelLeaseRequest(leaseRequestId)));
    }

    @Override
    @PreAuthorize("authentication.name==#request.userId &&" +
            "hasAnyAuthority(" +
            "#request.libraryId + ':" + ROLE_STUDENT + "'," +
            "#request.libraryId + ':" + ROLE_TEACHER + "')")
    public Mono<String> createLeaseRequest(LeaseRequest request) {
        return delegate.createLeaseRequest(request);
    }

    @Override
    public Mono<Void> acceptLeaseRequest(AcceptedLease acceptedLease) {
        return checkUserIsLibrarian(acceptedLease.getLeaseRequestId())
                .then(delegate.acceptLeaseRequest(acceptedLease));

    }

    @Override
    public Mono<Void> rejectLeaseRequest(RejectedLease rejectedLease) {
        return checkUserIsLibrarian(rejectedLease.getLeaseRequestId())
                .then(delegate.rejectLeaseRequest(rejectedLease));
    }

    private Mono<Boolean> checkUserIsLibrarian(String leaseRequestId) {
        return leaseRequestDao
                .getLeaseRequest(leaseRequestId)
                .map(LeaseRequest::getLibraryId)
                .map(libraryId -> libraryId + ":" + ROLE_LIBRARIAN)
                .flatMap(SecurityUtil::hasAuthority)
                .filter(Boolean::booleanValue)
                .switchIfEmpty(Mono.error(() -> new AccessDeniedException("Denied")));
    }
}
