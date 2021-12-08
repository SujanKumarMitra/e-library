package com.github.sujankumarmitra.libraryservice.v1.service.impl;

import com.github.sujankumarmitra.libraryservice.v1.config.PagingProperties;
import com.github.sujankumarmitra.libraryservice.v1.dao.LeaseRecordDao;
import com.github.sujankumarmitra.libraryservice.v1.dao.LeaseRequestDao;
import com.github.sujankumarmitra.libraryservice.v1.dao.PhysicalBookDao;
import com.github.sujankumarmitra.libraryservice.v1.exception.BookNotFoundException;
import com.github.sujankumarmitra.libraryservice.v1.exception.LeaseRequestAlreadyHandledException;
import com.github.sujankumarmitra.libraryservice.v1.exception.LeaseRequestNotFoundException;
import com.github.sujankumarmitra.libraryservice.v1.model.*;
import com.github.sujankumarmitra.libraryservice.v1.model.impl.DefaultMoney;
import com.github.sujankumarmitra.libraryservice.v1.service.ActiveLeaseService;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SynchronousSink;
import reactor.util.function.Tuples;

import java.math.BigDecimal;
import java.time.Duration;

import static com.github.sujankumarmitra.libraryservice.v1.model.LeaseStatus.ACCEPTED;
import static com.github.sujankumarmitra.libraryservice.v1.model.LeaseStatus.EXPIRED;
import static java.math.BigDecimal.ZERO;

/**
 * @author skmitra
 * @since Dec 08/12/21, 2021
 */
@Service
@AllArgsConstructor
public class DefaultActiveLeaseService implements ActiveLeaseService {
    @NonNull
    private final PagingProperties pagingProperties;
    @NonNull
    private final LeaseRecordDao leaseRecordDao;
    @NonNull
    private final LeaseRequestDao leaseRequestDao;
    @NonNull
    private final PhysicalBookDao physicalBookDao;

    @Override
    public Flux<LeaseRecord> getAllActiveLeases(int pageNo) {
        int pageSize = pagingProperties.getDefaultPageSize();

        int skip = pageSize * pageNo;

        return leaseRecordDao
                .getActiveLeaseRecords(skip, pageSize);
    }

    @Override
    public Flux<LeaseRecord> getAllActiveLeases(String userId, int pageNo) {
        int pageSize = pagingProperties.getDefaultPageSize();

        int skip = pageSize * pageNo;
        return leaseRecordDao
                .getActiveLeaseRecordsByUserId(userId, skip, pageSize);
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
//        TODO send notification to students about lease relinquishment
        return leaseRecordDao
                .markAsRelinquished(leaseRequestId)
                .then(leaseRequestDao.setLeaseStatus(leaseRequestId, EXPIRED));
    }

    @Override
    @Transactional
    public Mono<Void> invalidateStateEBookLeases() {
        return leaseRecordDao
                .getStaleEBookLeaseRecordIds()
                .flatMap(this::relinquishActiveLease)
                .then();
    }
}