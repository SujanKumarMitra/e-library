package com.github.sujankumarmitra.libraryservice.v1.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.sujankumarmitra.libraryservice.v1.config.PagingProperties;
import com.github.sujankumarmitra.libraryservice.v1.dao.AcceptedLeaseDao;
import com.github.sujankumarmitra.libraryservice.v1.dao.LeaseRequestDao;
import com.github.sujankumarmitra.libraryservice.v1.dao.LibrarianDao;
import com.github.sujankumarmitra.libraryservice.v1.dao.RejectedLeaseDao;
import com.github.sujankumarmitra.libraryservice.v1.exception.InternalError;
import com.github.sujankumarmitra.libraryservice.v1.exception.*;
import com.github.sujankumarmitra.libraryservice.v1.model.*;
import com.github.sujankumarmitra.libraryservice.v1.model.impl.DefaultAcceptedLease;
import com.github.sujankumarmitra.libraryservice.v1.model.impl.DefaultNotification;
import com.github.sujankumarmitra.libraryservice.v1.service.BookService;
import com.github.sujankumarmitra.libraryservice.v1.service.LeaseRequestService;
import com.github.sujankumarmitra.libraryservice.v1.service.NotificationService;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SynchronousSink;

import java.util.HashMap;
import java.util.Map;

import static com.github.sujankumarmitra.libraryservice.v1.model.LeaseStatus.*;
import static java.lang.Boolean.FALSE;

/**
 * @author skmitra
 * @since Dec 07/12/21, 2021
 */
@Service
@AllArgsConstructor
@Slf4j
public class DefaultLeaseRequestService implements LeaseRequestService {

    @NonNull
    private final LeaseRequestDao leaseRequestDao;
    @NonNull
    private final AcceptedLeaseDao acceptedLeaseDao;
    @NonNull
    private final RejectedLeaseDao rejectedLeaseDao;
    @NonNull
    private final LibrarianDao librarianDao;
    @NonNull
    private final BookService bookService;
    @NonNull
    private final NotificationService notificationService;
    @NonNull
    private final ObjectMapper objectMapper;
    @NonNull
    private final PagingProperties pagingProperties;

    @Override
    public Flux<LeaseRequest> getPendingLeaseRequests(String libraryId, int pageNo) {
        int pageSize = pagingProperties.getDefaultPageSize();
        int skip = pageNo * pageSize;

        return leaseRequestDao.getPendingLeaseRequests(libraryId, skip, pageSize);
    }

    @Override
    public Flux<LeaseRequest> getPendingLeaseRequests(String libraryId, @NonNull String userId, int pageNo) {
        int pageSize = pagingProperties.getDefaultPageSize();
        int skip = pageNo * pageSize;

        return leaseRequestDao.getPendingLeaseRequests(libraryId, userId, skip, pageSize);
    }

    @Override
    public Mono<Void> cancelLeaseRequest(@NonNull String leaseRequestId) {
        return getValidLeaseRequest(leaseRequestId)
                .then(leaseRequestDao.deletePendingLeaseRequest(leaseRequestId))
                .onErrorResume(this::isIgnorableError, err -> Mono.empty());
    }

    private boolean isIgnorableError(Throwable th) {
        return th instanceof LeaseRequestNotFoundException ||
                th instanceof LeaseRequestAlreadyHandledException;
    }

    @Override
    public Mono<String> createLeaseRequest(@NonNull LeaseRequest request) {
        return bookService
                .getBook(request.getBookId())
                .filter(book -> book.getLibraryId().equals(request.getLibraryId()))
                .switchIfEmpty(Mono.error(() -> new LibraryIdMismatchException("given book does not belong to libraryId '" + request.getLibraryId() + "'")))
                .handle(this::emitErrorIfNoCopiesAvailable)
                .map(Book::getLibraryId)
                .flatMap(libraryId -> leaseRequestDao
                        .createLeaseRequest(request)
                        .flatMap(leaseRequestId -> sendNotificationForNewLeaseRequest(leaseRequestId, libraryId)
                                .thenReturn(leaseRequestId)));
    }

    private Mono<Void> sendNotificationForNewLeaseRequest(String leaseRequestId, String libraryId) {

        return librarianDao
                .getLibrarians(libraryId)
                .map(Librarian::getUserId)
                .map(librarianId -> {
                    DefaultNotification notification = new DefaultNotification();

                    notification.setConsumerId(librarianId);
                    notification.setPayload(createPayload(leaseRequestId));
                    notification.setCreatedAt(System.currentTimeMillis());

                    return notification;
                })
                .flatMap(this::sendNotification)
                .then();

    }

    private String createPayload(String leaseRequestId) {
        Map<String, String> payload = new HashMap<>();

        payload.put("event", "NEW_LEASE_REQUEST");
        payload.put("leaseRequestId", leaseRequestId);

        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            log.warn("Error in serializing payload for notification", e);
            throw new InternalError("error in serializing json payload");
        }
    }

    private void emitErrorIfNoCopiesAvailable(Book book, SynchronousSink<Book> sink) {
        if (!(book instanceof PhysicalBook)) {
            sink.next(book);
            return;
        }
        if ((((PhysicalBook) book).getCopiesAvailable()) > 0) {
            sink.next(book);
        } else {
            sink.error(new InsufficientCopiesAvailableException(book.getId()));
        }
    }

    /**
     * <ol>
     * <li>
     * Check if {@link LeaseRequest} exists or not, if not then emit {@link LeaseRequestNotFoundException}
     * </li>
     * <li>
     * If {@link LeaseRequest#getStatus()} is not in {@link LeaseStatus#PENDING} state, then emit {@link LeaseRequestAlreadyHandledException}
     * </li>
     * <li>
     * Else,
     *     <ol>
     *         <li>
     *              Decrement {@link PhysicalBook#getCopiesAvailable()}, if {@link LeaseRequest#getBookId()}is for physicalBook
     *          </li>
     *          <li>
     *              Assign Read Permissions for {@link EBook} if {@link LeaseRequest#getBookId()}is for physicalBook
     *          </li>
     *          <li>
     *              Set {@link LeaseRequest#getStatus()} to {@link LeaseStatus#ACCEPTED}
     *          </li>
     *          <li>
     *              Create {@link AcceptedLease}
     *          </li>
     *          <li>
     *              Send notification to {@link LeaseRequest#getUserId()} about {@link LeaseStatus#ACCEPTED} (Optional, error signal should not be propagated)
     *          </li>
     *
     *     </ol>
     * </li>
     * </ol>
     *
     * @param acceptedLease the accepted lease information
     * @return a {@link Mono} that publishes a complete signal when all the above operations succeed
     */
    @Override
    @Transactional
    public Mono<Void> acceptLeaseRequest(AcceptedLease acceptedLease) {
        String leaseRequestId = acceptedLease.getLeaseRequestId();

        return getValidLeaseRequest(leaseRequestId)
                .flatMap(leaseRequest -> {
                    Mono<Void> createRecordMono = acceptedLeaseDao.createLeaseRecord(buildLeaseRecord(acceptedLease));
                    Mono<Void> updateStatusMono = leaseRequestDao.setLeaseStatus(leaseRequestId, ACCEPTED);
                    Mono<Void> sendNotificationMono = sendNotificationForLeaseRequestHandling(leaseRequest, ACCEPTED);
                    Mono<Void> handleLeaseAcceptMono = bookService.onLeaseAccept(acceptedLease);

                    return handleLeaseAcceptMono
                            .then(Mono.when(createRecordMono, updateStatusMono))
                            .then(sendNotificationMono);
                });
    }

    /**
     * <ol>
     * <li>
     * Check if {@link LeaseRequest} exists or not, if not then emit {@link LeaseRequestNotFoundException}
     * </li>
     * <li>
     * If {@link LeaseRequest#getStatus()} is not in {@link LeaseStatus#PENDING} state, then emit {@link LeaseRequestAlreadyHandledException}
     * </li>
     * <li>
     * Else,
     *     <ol>
     *          <li>
     *              Set {@link LeaseRequest#getStatus()} to {@link LeaseStatus#REJECTED}
     *          </li>
     *          <li>
     *              Create {@link RejectedLease}
     *          </li>
     *          <li>
     *              Send notification to {@link LeaseRequest#getUserId()} about {@link LeaseStatus#REJECTED} (Optional, error signal should not be propagated)
     *          </li>
     *
     *     </ol>
     * </li>
     * </ol>
     *
     * @param rejectedLease the rejected lease information
     * @return a {@link Mono} that publishes a complete signal when all the above operations succeed
     */
    @Override
    @Transactional
    public Mono<Void> rejectLeaseRequest(@NonNull RejectedLease rejectedLease) {
        String leaseRequestId = rejectedLease.getLeaseRequestId();
        return getValidLeaseRequest(leaseRequestId)
                .flatMap(leaseRequest -> {
                    Mono<Void> updateStatusMono = leaseRequestDao.setLeaseStatus(leaseRequestId, REJECTED);
                    Mono<Void> createRejectedLeaseMono = rejectedLeaseDao.createRejectedLease(rejectedLease);
                    Mono<Void> sendNotificationMono = sendNotificationForLeaseRequestHandling(leaseRequest, REJECTED);

                    return Mono.when(updateStatusMono, createRejectedLeaseMono)
                            .then(sendNotificationMono);
                });
    }

    private Mono<LeaseRequest> getValidLeaseRequest(String leaseRequestId) {
        return leaseRequestDao
                .getLeaseRequest(leaseRequestId)
                .switchIfEmpty(Mono.error(new LeaseRequestNotFoundException(leaseRequestId)))
                .handle(this::emitErrorIfNotInPendingState);
    }

    private AcceptedLease buildLeaseRecord(AcceptedLease acceptedLease) {
        return new DefaultAcceptedLease(
                acceptedLease.getLeaseRequestId(),
                acceptedLease.getStartTimeInEpochMilliseconds(),
                acceptedLease.getDurationInMilliseconds(),
                FALSE);
    }

    private void emitErrorIfNotInPendingState(LeaseRequest request, SynchronousSink<LeaseRequest> sink) {
        LeaseStatus status = request.getStatus();
        if (status != PENDING) {
            sink.error(new LeaseRequestAlreadyHandledException(request.getId(), status));
        } else {
            sink.next(request);
        }
    }

    private Mono<Void> sendNotificationForLeaseRequestHandling(LeaseRequest request, LeaseStatus status) {
        DefaultNotification notification = new DefaultNotification();

        notification.setConsumerId(request.getUserId());
        notification.setCreatedAt(System.currentTimeMillis());
        notification.setPayload(createPayload(status, request.getId()));

        return sendNotification(notification);
    }

    private String createPayload(LeaseStatus status, String leaseRequestId) {
        Map<String, String> payload = new HashMap<>();

        payload.put("event", "LEASE_" + status.toString());
        payload.put("leaseRequestId", leaseRequestId);

        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            log.warn("Error in serializing payload for notification", e);
            throw new InternalError("error in serializing json payload");
        }
    }

    private Mono<Void> sendNotification(Notification notification) {
        return notificationService
                .sendNotification(notification)
                .doOnSuccess(aVoid -> log.info("Successfully sent notification"))
                .doOnError(err -> log.warn("Error in sending Notification :: {}", err.getMessage()))
                .onErrorResume(th -> Mono.empty());
    }

}
