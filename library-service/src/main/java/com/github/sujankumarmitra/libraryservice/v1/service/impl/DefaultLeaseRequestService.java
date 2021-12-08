package com.github.sujankumarmitra.libraryservice.v1.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.sujankumarmitra.libraryservice.v1.config.PagingProperties;
import com.github.sujankumarmitra.libraryservice.v1.dao.LeaseRecordDao;
import com.github.sujankumarmitra.libraryservice.v1.dao.LeaseRequestDao;
import com.github.sujankumarmitra.libraryservice.v1.dao.RejectedLeaseDao;
import com.github.sujankumarmitra.libraryservice.v1.exception.*;
import com.github.sujankumarmitra.libraryservice.v1.model.*;
import com.github.sujankumarmitra.libraryservice.v1.model.impl.DefaultLeaseRecord;
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

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
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
    private final LeaseRecordDao leaseRecordDao;
    @NonNull
    private final RejectedLeaseDao rejectedLeaseDao;
    @NonNull
    private final BookService bookService;
    @NonNull
    private final NotificationService notificationService;
    @NonNull
    private final ObjectMapper objectMapper;
    @NonNull
    private final PagingProperties pagingProperties;

    @Override
    public Flux<LeaseRequest> getPendingLeaseRequests(int pageNo) {
        int pageSize = pagingProperties.getDefaultPageSize();

        int skip = pageNo * pageSize;
        int limit = skip + pageSize;

        return leaseRequestDao.getPendingLeaseRequests(skip, limit);
    }

    @Override
    public Flux<LeaseRequest> getPendingLeaseRequests(@NonNull String userId, int pageNo) {
        int pageSize = pagingProperties.getDefaultPageSize();

        int skip = pageNo * pageSize;
        int limit = skip + pageSize;

        return leaseRequestDao.getPendingLeaseRequests(userId, skip, limit);
    }

    @Override
    public Mono<Void> deleteLeaseRequest(@NonNull String leaseRequestId) {
        return getValidLeaseRequest(leaseRequestId)
                .then(leaseRequestDao.deleteLeaseRequest(leaseRequestId));
    }

    @Override
    public Mono<String> createLeaseRequest(@NonNull LeaseRequest request) {
//        TODO Send notification to librarians
        return leaseRequestDao.createLeaseRequest(request);
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
     *              Create {@link LeaseRecord}
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
                    Mono<Void> createRecordMono = leaseRecordDao.createLeaseRecord(buildLeaseRecord(acceptedLease));
                    Mono<Void> updateStatusMono = leaseRequestDao.setLeaseStatus(leaseRequestId, ACCEPTED);
                    Mono<Void> sendNotificationMono = createAndSendNotification(leaseRequest, ACCEPTED);
                    Mono<Void> handleLeaseAcceptMono = bookService.handleLeaseAccept(acceptedLease);

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
                    Mono<Void> sendNotificationMono = createAndSendNotification(leaseRequest, REJECTED);

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

    private LeaseRecord buildLeaseRecord(AcceptedLease acceptedLease) {
        return new DefaultLeaseRecord(
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

    private Mono<Void> createAndSendNotification(LeaseRequest request, LeaseStatus status) {
        DefaultNotification notification = new DefaultNotification();

        notification.setConsumerId(request.getUserId());
        notification.setTimestamp(System.currentTimeMillis());
        notification.setPayload(createPayload(status, request.getId()));

        return sendNotification(notification);
    }

    private String createPayload(LeaseStatus status, String leaseRequestId) {
        Map<String, String> payload = new HashMap<>();

        payload.put("status", status.toString());
        payload.put("leaseRequestId", leaseRequestId);

        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            log.warn("Error in serializing payload for notification", e);
            throw new ApiOperationException() {
                @Override
                @SuppressWarnings("unchecked")
                public Collection<ErrorDetails> getErrors() {
                    return List.of(
                            new DefaultErrorDetails("error in serializing json payload"));
                }
            };
        }
    }

    private Mono<Void> sendNotification(Notification notification) {
        return Mono.create(sink -> notificationService
                .sendNotification(notification)
                .subscribe(sink::success,
                        err -> {
                            log.warn("Error in sending Notification :: {}", err.getMessage());
                            sink.success();
                        },
                        sink::success));
    }

}
