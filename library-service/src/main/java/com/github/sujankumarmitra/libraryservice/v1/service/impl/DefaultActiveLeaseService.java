package com.github.sujankumarmitra.libraryservice.v1.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.sujankumarmitra.libraryservice.v1.config.PagingProperties;
import com.github.sujankumarmitra.libraryservice.v1.dao.LeaseRecordDao;
import com.github.sujankumarmitra.libraryservice.v1.dao.LeaseRequestDao;
import com.github.sujankumarmitra.libraryservice.v1.dao.PhysicalBookDao;
import com.github.sujankumarmitra.libraryservice.v1.exception.BookNotFoundException;
import com.github.sujankumarmitra.libraryservice.v1.exception.InternalError;
import com.github.sujankumarmitra.libraryservice.v1.exception.LeaseRequestAlreadyHandledException;
import com.github.sujankumarmitra.libraryservice.v1.exception.LeaseRequestNotFoundException;
import com.github.sujankumarmitra.libraryservice.v1.model.*;
import com.github.sujankumarmitra.libraryservice.v1.model.impl.DefaultMoney;
import com.github.sujankumarmitra.libraryservice.v1.model.impl.DefaultNotification;
import com.github.sujankumarmitra.libraryservice.v1.service.ActiveLeaseService;
import com.github.sujankumarmitra.libraryservice.v1.service.BookService;
import com.github.sujankumarmitra.libraryservice.v1.service.NotificationService;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SynchronousSink;
import reactor.util.function.Tuples;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import static com.github.sujankumarmitra.libraryservice.v1.model.LeaseStatus.ACCEPTED;
import static com.github.sujankumarmitra.libraryservice.v1.model.LeaseStatus.EXPIRED;
import static java.math.BigDecimal.ZERO;

/**
 * @author skmitra
 * @since Dec 08/12/21, 2021
 */
@Service
@Slf4j
@AllArgsConstructor
public class DefaultActiveLeaseService implements ActiveLeaseService {
    @NonNull
    private final LeaseRecordDao leaseRecordDao;
    @NonNull
    private final LeaseRequestDao leaseRequestDao;
    @NonNull
    private final PhysicalBookDao physicalBookDao;
    @NonNull
    private final PagingProperties pagingProperties;
    @NonNull
    private final NotificationService notificationService;
    @NonNull
    private final BookService bookService;
    @NonNull
    private final ObjectMapper objectMapper;


    @Override
    public Flux<LeaseRecord> getAllActiveLeases(String libraryId, int pageNo) {
        int pageSize = pagingProperties.getDefaultPageSize();

        int skip = pageSize * pageNo;

        return leaseRecordDao
                .getActiveLeaseRecords(libraryId, skip, pageSize);
    }

    @Override
    public Flux<LeaseRecord> getAllActiveLeases(String libraryId, String userId, int pageNo) {
        int pageSize = pagingProperties.getDefaultPageSize();

        int skip = pageSize * pageNo;
        return leaseRecordDao
                .getActiveLeaseRecords(libraryId, userId, skip, pageSize);
    }

    @Override
    @Transactional
    public Mono<Money> getFineForActiveLease(String leaseRequestId) {
        long currentTimeMillis = System.currentTimeMillis();
        return leaseRequestDao
                .getLeaseRequest(leaseRequestId)
                .switchIfEmpty(Mono.error(new LeaseRequestNotFoundException(leaseRequestId)))
                .handle(this::filterIfNotAccepted)
                .flatMap(leaseRequest -> physicalBookDao
                        .getBook(leaseRequest.getBookId())
                        .switchIfEmpty(Mono.error(new BookNotFoundException(leaseRequest.getBookId())))
                        .map(PhysicalBook::getFinePerDay)
                        .flatMap(fine -> leaseRecordDao
                                .getLeaseRecord(leaseRequestId)
                                .map(leaseRecord -> Tuples.of(fine, leaseRecord))))
                .map(tuple2 -> computeFine(tuple2.getT1(), tuple2.getT2(), currentTimeMillis));
    }

    private Money computeFine(Money finePerDay, LeaseRecord leaseRecord, long currentTimeMillis) {

        long duration = leaseRecord.getDurationInMilliseconds();
        String currencyCode = finePerDay.getCurrencyCode();

        if (duration == AcceptedLease.INFINITE_LEASE_DURATION) {
            return new DefaultMoney(ZERO, currencyCode);
        }

        long leaseStartTime = leaseRecord.getStartTimeInEpochMilliseconds();
        long expirationTime = leaseStartTime + duration;

        long elapsedMillisSinceExpired = currentTimeMillis - expirationTime;
        if (elapsedMillisSinceExpired <= 0) {
            return new DefaultMoney(ZERO, currencyCode);
        }

        long elapsedDaysSinceExpired = Duration.ofMillis(elapsedMillisSinceExpired).toDays();

        return new DefaultMoney(computeAmount(finePerDay, elapsedDaysSinceExpired), currencyCode);
    }

    private BigDecimal computeAmount(Money finePerDay, long elapsedDaysSinceExpired) {

        BigDecimal amount = finePerDay.getAmount();
        BigDecimal elapsedDaysInBigDecimal = BigDecimal.valueOf(elapsedDaysSinceExpired);

        return amount.multiply(elapsedDaysInBigDecimal);
    }

    private void filterIfNotAccepted(LeaseRequest leaseRequest, SynchronousSink<LeaseRequest> sink) {
        LeaseStatus leaseStatus = leaseRequest.getStatus();
        if (leaseStatus != ACCEPTED) {
            sink.error(new LeaseRequestAlreadyHandledException(leaseRequest.getId(), leaseStatus));
        } else {
            sink.next(leaseRequest);
        }
    }

    @Override
    @Transactional
    public Mono<Void> relinquishActiveLease(String leaseRequestId) {
        return leaseRequestDao
                .getLeaseRequest(leaseRequestId)
                .switchIfEmpty(Mono.error(new LeaseRequestNotFoundException(leaseRequestId)))
                .handle(this::emitErrorIfAlreadyHandled)
                .flatMap(leaseRequest -> leaseRecordDao
                        .markAsRelinquished(leaseRequestId)
                        .then(leaseRequestDao.setLeaseStatus(leaseRequestId, EXPIRED))
                        .then(bookService.onLeaseRelinquish(leaseRequest)))
                .then(sendNotification(leaseRequestId));
    }

    private void emitErrorIfAlreadyHandled(LeaseRequest leaseRequest, SynchronousSink<LeaseRequest> sink) {
        if (leaseRequest.getStatus() != ACCEPTED) {
            sink.error(new LeaseRequestAlreadyHandledException(leaseRequest.getId(), EXPIRED));
        } else {
            sink.next(leaseRequest);
        }
    }

    @Override
    @Transactional
    public Mono<Void> invalidateStaleEBookLeases() {
        return leaseRecordDao
                .getStaleEBookLeaseRecordIds()
                .flatMap(this::relinquishActiveLease)
                .then();
    }

    private Mono<Void> sendNotification(String leaseRequestId) {
        return leaseRequestDao
                .getLeaseRequest(leaseRequestId)
                .map(leaseRequest -> {
                    DefaultNotification notification = new DefaultNotification();

                    notification.setConsumerId(leaseRequest.getUserId());
                    notification.setCreatedAt(System.currentTimeMillis());
                    notification.setPayload(createPayload(leaseRequest));

                    return notification;
                })
                .flatMap(notificationService::sendNotification)
                .doOnSuccess(aVoid -> log.info("Sent notification for lease expiry"))
                .doOnError(err -> log.info("Error in sending notification ", err))
                .onErrorResume(th -> Mono.empty());
    }

    private String createPayload(LeaseRequest request) {
        Map<String, String> payload = new HashMap<>();

        payload.put("event", "LEASE_EXPIRED");
        payload.put("leaseRequestId", request.getId());

        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            log.warn("Error in serializing notification payload", e);
            throw new InternalError("failed to serialize json payload");
        }
    }
}
