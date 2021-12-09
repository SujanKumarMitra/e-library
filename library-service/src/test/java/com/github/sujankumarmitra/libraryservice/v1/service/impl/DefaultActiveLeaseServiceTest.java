package com.github.sujankumarmitra.libraryservice.v1.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.sujankumarmitra.libraryservice.v1.config.PagingProperties;
import com.github.sujankumarmitra.libraryservice.v1.dao.LeaseRecordDao;
import com.github.sujankumarmitra.libraryservice.v1.dao.LeaseRequestDao;
import com.github.sujankumarmitra.libraryservice.v1.dao.PhysicalBookDao;
import com.github.sujankumarmitra.libraryservice.v1.dao.impl.entity.R2dbcLeaseRecord;
import com.github.sujankumarmitra.libraryservice.v1.dao.impl.entity.R2dbcLeaseRequest;
import com.github.sujankumarmitra.libraryservice.v1.dao.impl.entity.R2dbcMoney;
import com.github.sujankumarmitra.libraryservice.v1.dao.impl.entity.R2dbcPhysicalBook;
import com.github.sujankumarmitra.libraryservice.v1.exception.BookNotFoundException;
import com.github.sujankumarmitra.libraryservice.v1.exception.LeaseRequestNotFoundException;
import com.github.sujankumarmitra.libraryservice.v1.model.Money;
import com.github.sujankumarmitra.libraryservice.v1.model.impl.DefaultMoney;
import com.github.sujankumarmitra.libraryservice.v1.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import static com.github.sujankumarmitra.libraryservice.v1.model.LeaseStatus.ACCEPTED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

/**
 * @author skmitra
 * @since Dec 08/12/21, 2021
 */
@Slf4j
@ExtendWith(MockitoExtension.class)
class DefaultActiveLeaseServiceTest {

    private DefaultActiveLeaseService activeLeaseService;
    @Mock
    private PagingProperties pagingProperties;
    @Mock
    private LeaseRecordDao leaseRecordDao;
    @Mock
    private LeaseRequestDao leaseRequestDao;
    @Mock
    private PhysicalBookDao physicalBookDao;
    @Mock
    private NotificationService notificationService;

    @BeforeEach
    void setUp() {
        activeLeaseService = new DefaultActiveLeaseService(
                leaseRecordDao,
                leaseRequestDao,
                physicalBookDao,
                pagingProperties,
                notificationService,
                new ObjectMapper());
    }

    @Test
    void givenValidLeaseRequest_whenComputeAmount_shouldCorrectlyComputeAmount() {

        UUID validPhysicalBookId = UUID.randomUUID();
        UUID validLeaseRequestId = UUID.randomUUID();

        R2dbcMoney finePerDay = new R2dbcMoney();

        finePerDay.setAmount(new BigDecimal("10.00"));
        finePerDay.setCurrencyCode("INR");

        R2dbcPhysicalBook book = new R2dbcPhysicalBook();
        book.setId(validPhysicalBookId);
        book.setFinePerDay(finePerDay);

        Mockito.doReturn(Mono.fromSupplier(() -> book))
                .when(physicalBookDao)
                .getBook(validPhysicalBookId.toString());

        R2dbcLeaseRecord leaseRecord = new R2dbcLeaseRecord();

        leaseRecord.setLeaseRequestId(validLeaseRequestId);
        leaseRecord.setStartTimeInEpochMilliseconds(daysFromToday(10));
        leaseRecord.setDurationInMilliseconds(Duration.ofDays(3).toMillis());
        leaseRecord.setRelinquished(false);

        Mockito.doReturn(Mono.fromSupplier(() -> leaseRecord))
                .when(leaseRecordDao)
                .getLeaseRecord(validLeaseRequestId.toString());


        R2dbcLeaseRequest leaseRequest = new R2dbcLeaseRequest();
        leaseRequest.setId(validLeaseRequestId);
        leaseRequest.setStatus(ACCEPTED);
        leaseRequest.setBookId(validPhysicalBookId);
        leaseRequest.setTimestamp(daysFromToday(11));

        Mockito.doReturn(Mono.fromSupplier(() -> leaseRequest))
                .when(leaseRequestDao)
                .getLeaseRequest(validLeaseRequestId.toString());


        Money expectedFine = new DefaultMoney(new BigDecimal("70.00"), "INR");

        activeLeaseService
                .getFineForActiveLease(validLeaseRequestId.toString())
                .as(StepVerifier::create)
                .expectSubscription()
                .consumeNextWith(actualFine -> {
                    log.info("Expected {}", expectedFine);
                    log.info("Expected {}", actualFine);

                    assertThat(actualFine).isEqualTo(expectedFine);
                })
                .expectComplete()
                .verify();
    }

    @Test
    void givenNonExistingLeaseRequestId_whenComputeAmount_shouldEmitError() {
        Mockito.doReturn(Mono.empty())
                .when(leaseRequestDao)
                .getLeaseRequest(any());

        activeLeaseService
                .getFineForActiveLease(UUID.randomUUID().toString())
                .as(StepVerifier::create)
                .expectSubscription()
                .expectError(LeaseRequestNotFoundException.class)
                .verify();


    }

    @Test
    void givenLeaseRequestIdWhichIsNotForPhysicalBook_whenComputeAmount_shouldEmitError() {
        UUID validLeaseRequestId = UUID.randomUUID();
        Mockito.doReturn(Mono.empty())
                .when(physicalBookDao)
                .getBook(any());

        R2dbcLeaseRequest leaseRequest = new R2dbcLeaseRequest();
        leaseRequest.setId(validLeaseRequestId);
        leaseRequest.setStatus(ACCEPTED);
        leaseRequest.setBookId(UUID.randomUUID());
        leaseRequest.setTimestamp(daysFromToday(11));

        Mockito.doReturn(Mono.fromSupplier(() -> leaseRequest))
                .when(leaseRequestDao)
                .getLeaseRequest(validLeaseRequestId.toString());

        activeLeaseService
                .getFineForActiveLease(validLeaseRequestId.toString())
                .as(StepVerifier::create)
                .expectSubscription()
                .expectErrorMatches(err -> {
                    log.info("Error:: ", err);
                    return err instanceof BookNotFoundException;
                })
                .log()
                .verify();
    }


    private long daysFromToday(long days) {
        return Instant.now().minus(Duration.ofDays(days)).toEpochMilli();
    }

}