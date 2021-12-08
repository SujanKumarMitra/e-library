package com.github.sujankumarmitra.libraryservice.v1.dao.impl;

import com.github.sujankumarmitra.libraryservice.v1.dao.impl.entity.R2dbcLeaseRecord;
import com.github.sujankumarmitra.libraryservice.v1.dao.impl.entity.R2dbcLeaseRequest;
import com.github.sujankumarmitra.libraryservice.v1.exception.LeaseRecordAlreadyExistsException;
import com.github.sujankumarmitra.libraryservice.v1.exception.LeaseRequestNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.UUID;

import static java.lang.Boolean.FALSE;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author skmitra
 * @since Dec 07/12/21, 2021
 */
@Slf4j
class R2dbcPostgresqlLeaseRecordDaoTest extends AbstractDataR2dbcPostgreSQLContainerDependentTest {

    private R2dbcPostgresqlLeaseRecordDao leaseRecordDao;
    @Autowired
    private R2dbcEntityTemplate entityTemplate;

    @BeforeEach
    void setUp() {
        leaseRecordDao = new R2dbcPostgresqlLeaseRecordDao(entityTemplate);
    }

    @AfterEach
    void tearDown() {
        entityTemplate
                .getDatabaseClient()
                .sql("DELETE FROM accepted_lease_requests")
                .fetch()
                .rowsUpdated()
                .block();

        entityTemplate
                .getDatabaseClient()
                .sql("DELETE FROM lease_requests")
                .fetch()
                .rowsUpdated()
                .block();

        entityTemplate
                .getDatabaseClient()
                .sql("DELETE FROM physical_books")
                .fetch()
                .rowsUpdated()
                .block();

        entityTemplate
                .getDatabaseClient()
                .sql("DELETE FROM books")
                .fetch()
                .rowsUpdated()
                .block();
    }

    @Test
    void givenValidLeaseRecord_whenCreate_shouldCreate() {

        R2dbcLeaseRecord leaseRecord = new R2dbcLeaseRecord();

        leaseRecord.setStartTimeInEpochMilliseconds(System.currentTimeMillis());
        leaseRecord.setRelinquished(FALSE);
        leaseRecord.setDurationInMilliseconds(System.currentTimeMillis() + Duration.ofDays(180).toMillis());

        BookDaoTestUtils
                .insertDummyBook(entityTemplate.getDatabaseClient())
                .flatMap(book -> LeaseRequestDaoTestUtils
                        .insertLeaseRequest(entityTemplate.getDatabaseClient(), book.getUuid()))
                .map(R2dbcLeaseRequest::getUuid)
                .doOnNext(leaseRecord::setLeaseRequestId)
                .then(leaseRecordDao.createLeaseRecord(leaseRecord))
                .then(entityTemplate
                        .select(R2dbcLeaseRecord.class)
                        .from("accepted_lease_requests")
                        .first())
                .as(StepVerifier::create)
                .expectSubscription()
                .consumeNextWith(savedLeaseRecord -> {
                    log.info("Expected {}", leaseRecord);
                    log.info("Actual {}", savedLeaseRecord);

                    assertThat(savedLeaseRecord).isEqualTo(leaseRecord);
                })
                .expectComplete()
                .verify();

    }

    @Test
    void givenInvalidLeaseRequestId_whenCreate_shouldEmitError() {

        R2dbcLeaseRecord leaseRecord = new R2dbcLeaseRecord();

        leaseRecord.setLeaseRequestId(UUID.randomUUID());
        leaseRecord.setStartTimeInEpochMilliseconds(System.currentTimeMillis());
        leaseRecord.setRelinquished(FALSE);
        leaseRecord.setDurationInMilliseconds(System.currentTimeMillis() + Duration.ofDays(180).toMillis());

        leaseRecordDao.createLeaseRecord(leaseRecord)
                .as(StepVerifier::create)
                .expectSubscription()
                .consumeErrorWith(err -> {
                    log.info("Error ", err);
                    assertThat(err).isExactlyInstanceOf(LeaseRequestNotFoundException.class);
                })
                .verify();

    }

    @Test
    void givenExistingLeaseRecordWithLeaseRequestId_whenCreate_shouldEmitError() {

        R2dbcLeaseRecord leaseRecord = new R2dbcLeaseRecord();

        leaseRecord.setStartTimeInEpochMilliseconds(System.currentTimeMillis());
        leaseRecord.setRelinquished(FALSE);
        leaseRecord.setDurationInMilliseconds(System.currentTimeMillis() + Duration.ofDays(180).toMillis());

        BookDaoTestUtils
                .insertDummyBook(entityTemplate.getDatabaseClient())
                .flatMap(book -> LeaseRequestDaoTestUtils
                        .insertLeaseRequest(entityTemplate.getDatabaseClient(), book.getUuid()))
                .map(R2dbcLeaseRequest::getUuid)
                .doOnNext(leaseRecord::setLeaseRequestId)
                .then(Mono.defer(() -> entityTemplate
                        .getDatabaseClient()
                        .sql(R2dbcPostgresqlLeaseRecordDao.INSERT_STATEMENT)
                        .bind("$1", leaseRecord.getLeaseRequestUuid())
                        .bind("$2", leaseRecord.getStartTimeInEpochMilliseconds())
                        .bind("$3", leaseRecord.getDurationInMilliseconds())
                        .bind("$4", leaseRecord.isRelinquished())
                        .fetch()
                        .rowsUpdated()))
                .then(leaseRecordDao.createLeaseRecord(leaseRecord))
                .as(StepVerifier::create)
                .expectSubscription()
                .consumeErrorWith(err -> {
                    log.info("Error", err);
                    assertThat(err).isExactlyInstanceOf(LeaseRecordAlreadyExistsException.class);
                })
                .verify();

    }

    @Test
    void givenValidLeaseRequestId_whenGetLeaseRecord_shouldGetRecord() {
//        TODO
    }

    @Test
    void givenNonExistingLeaseRequestId_whenGetLeaseRecord_shouldEmitEmpty() {
//        TODO
    }

    @Test
    void givenMalformedLeaseRequestUuid_whenGetLeaseRecord_shouldEmitEmpty() {
//        TODO
    }

    @Test
    void givenValidLeaseRequestId_whenMarkAsRelinquished_shouldRelinquish() {
//        TODO
    }

    @Test
    void givenNonExistingLeaseRequestId_whenMarkAsRelinquished_shouldEmitEmpty() {
//        TODO
    }

    @Test
    void givenMalformedLeaseRequestUuid_whenMarkAsRelinquished_shouldRelinquish() {
//        TODO
    }


    @Test
    void givenSetOfActiveLeaseRecords_getActiveLeaseRecords_shouldGetActiveLeaseRecords() {
//        TODO
    }

    @Test
    void givenSetOfActiveLeaseRecords_getActiveLeaseRecordsByUserId_shouldGetActiveLeaseRecords() {
//        TODO
    }

    @Test
    void givenSetOfStaleEBookLeaseRecords_getgetStaleEBookLeaseRecordIds_shouldGetgetStaleEBookLeaseRecordIds() {
//        TODO
    }
}