package com.github.sujankumarmitra.libraryservice.v1.service.impl;

import com.github.sujankumarmitra.libraryservice.v1.dao.LeaseRequestDao;
import com.github.sujankumarmitra.libraryservice.v1.model.LeaseRecord;
import com.github.sujankumarmitra.libraryservice.v1.model.LeaseRequest;
import com.github.sujankumarmitra.libraryservice.v1.model.Money;
import com.github.sujankumarmitra.libraryservice.v1.service.ActiveLeaseService;
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
 * @since Jan 29/01/22, 2022
 */
@Service
@Primary
@AllArgsConstructor
public class SecuredActiveLeaseService implements ActiveLeaseService {

    @NonNull
    private final ActiveLeaseService delegate;
    @NonNull
    private final LeaseRequestDao leaseRequestDao;

    @Override
    @PreAuthorize("hasAuthority(#libraryId + ':" + ROLE_LIBRARIAN + "')")
    public Flux<LeaseRecord> getAllActiveLeases(String libraryId, int pageNo) {
        return delegate.getAllActiveLeases(libraryId, pageNo);
    }

    @Override
    @PreAuthorize("hasAuthority(#libraryId + ':" + ROLE_LIBRARIAN + "') || " +
            "(authentication.name==#userId && hasAnyAuthority(" +
            "#libraryId + ':" + ROLE_STUDENT + "', " +
            "#libraryId + ':" + ROLE_TEACHER + "'))")
    public Flux<LeaseRecord> getAllActiveLeases(String libraryId, String userId, int pageNo) {
        return delegate.getAllActiveLeases(libraryId, userId, pageNo);
    }

    @Override
    public Mono<Money> getFineForActiveLease(String leaseRequestId) {
        return userIsLibrarian(leaseRequestId)
                .then(delegate.getFineForActiveLease(leaseRequestId));
    }

    @Override
    public Mono<Void> relinquishActiveLease(String leaseRequestId) {
        return userIsLibrarian(leaseRequestId)
                .then(delegate.relinquishActiveLease(leaseRequestId));
    }

    private Mono<Void> userIsLibrarian(String leaseRequestId) {
        return leaseRequestDao
                .getLeaseRequest(leaseRequestId)
                .map(LeaseRequest::getLibraryId)
                .map(libraryId -> libraryId + ":" + ROLE_LIBRARIAN)
                .filterWhen(SecurityUtil::hasAuthority)
                .switchIfEmpty(Mono.error(() -> new AccessDeniedException("Denied")))
                .then();
    }

    @Override
    public Mono<Void> invalidateStaleEBookLeases() {
        return delegate.invalidateStaleEBookLeases();
    }

}
