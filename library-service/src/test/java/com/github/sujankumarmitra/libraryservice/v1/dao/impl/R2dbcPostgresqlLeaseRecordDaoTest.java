package com.github.sujankumarmitra.libraryservice.v1.dao.impl;

import com.github.sujankumarmitra.libraryservice.v1.dao.impl.entity.R2dbcBook;
import com.github.sujankumarmitra.libraryservice.v1.dao.impl.entity.R2dbcLeaseRecord;
import com.github.sujankumarmitra.libraryservice.v1.dao.impl.entity.R2dbcLeaseRequest;
import com.github.sujankumarmitra.libraryservice.v1.exception.LeaseRecordAlreadyExistsException;
import com.github.sujankumarmitra.libraryservice.v1.exception.LeaseRequestNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.github.sujankumarmitra.libraryservice.v1.util.BookDaoTestUtils.insertDummyBook;
import static com.github.sujankumarmitra.libraryservice.v1.util.DaoTestUtils.truncateAllTables;
import static com.github.sujankumarmitra.libraryservice.v1.util.LeaseRequestDaoTestUtils.insertLeaseRequest;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.r2dbc.connection.init.ScriptUtils.executeSqlScript;

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
        truncateAllTables(entityTemplate.getDatabaseClient())
                .block();
    }

    @Test
    void givenValidLeaseRecord_whenCreate_shouldCreate() {

        R2dbcLeaseRecord leaseRecord = new R2dbcLeaseRecord();

        leaseRecord.setStartTimeInEpochMilliseconds(System.currentTimeMillis());
        leaseRecord.setRelinquished(FALSE);
        leaseRecord.setDurationInMilliseconds(System.currentTimeMillis() + Duration.ofDays(180).toMillis());

        insertDummyBook(entityTemplate.getDatabaseClient())
                .flatMap(book -> insertLeaseRequest(entityTemplate.getDatabaseClient(), book.getUuid()))
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

        insertDummyBook(entityTemplate.getDatabaseClient())
                .flatMap(book -> insertLeaseRequest(entityTemplate.getDatabaseClient(), book.getUuid()))
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
        R2dbcLeaseRecord leaseRecord = new R2dbcLeaseRecord();

        leaseRecord.setRelinquished(FALSE);
        leaseRecord.setStartTimeInEpochMilliseconds(System.currentTimeMillis());
        leaseRecord.setDurationInMilliseconds(Duration.ofDays(30).toMillis());

        insertDummyBook(entityTemplate.getDatabaseClient())
                .map(R2dbcBook::getUuid)
                .flatMap(id -> insertLeaseRequest(entityTemplate.getDatabaseClient(), id))
                .map(R2dbcLeaseRequest::getUuid)
                .doOnNext(leaseRecord::setLeaseRequestId)
                .then(Mono.defer(() -> entityTemplate
                        .getDatabaseClient()
                        .sql(R2dbcPostgresqlLeaseRecordDao.INSERT_STATEMENT)
                        .bind("$1", leaseRecord.getLeaseRequestUuid())
                        .bind("$2", leaseRecord.getStartTimeInEpochMilliseconds())
                        .bind("$3", leaseRecord.getDurationInMilliseconds())
                        .bind("$4", leaseRecord.getRelinquished())
                        .fetch()
                        .rowsUpdated()
                        .then()))
                .then(Mono.defer(() -> leaseRecordDao.getLeaseRecord(leaseRecord.getLeaseRequestId())))
                .as(StepVerifier::create)
                .expectSubscription()
                .consumeNextWith(actualLeaseRecord -> {
                    log.info("Expected {}", leaseRecord);
                    log.info("Actual {}", actualLeaseRecord);

                    assertThat(actualLeaseRecord).isEqualTo(leaseRecord);
                })
                .expectComplete()
                .verify();
    }

    @Test
    void givenNonExistingLeaseRequestId_whenGetLeaseRecord_shouldEmitEmpty() {
        leaseRecordDao
                .getLeaseRecord(UUID.randomUUID().toString())
                .as(StepVerifier::create)
                .expectSubscription()
                .expectComplete()
                .verify();
    }

    @Test
    void givenMalformedLeaseRequestUuid_whenGetLeaseRecord_shouldEmitEmpty() {
        leaseRecordDao
                .getLeaseRecord("malformed")
                .as(StepVerifier::create)
                .expectSubscription()
                .expectComplete()
                .verify();
    }

    @Test
    void givenValidLeaseRequestId_whenMarkAsRelinquished_shouldRelinquish() {
        R2dbcLeaseRecord leaseRecord = new R2dbcLeaseRecord();

        leaseRecord.setRelinquished(FALSE);
        leaseRecord.setStartTimeInEpochMilliseconds(System.currentTimeMillis());
        leaseRecord.setDurationInMilliseconds(Duration.ofDays(30).toMillis());

        insertDummyBook(entityTemplate.getDatabaseClient())
                .map(R2dbcBook::getUuid)
                .flatMap(id -> insertLeaseRequest(entityTemplate.getDatabaseClient(), id))
                .map(R2dbcLeaseRequest::getUuid)
                .doOnNext(leaseRecord::setLeaseRequestId)
                .then(Mono.defer(() -> entityTemplate
                        .getDatabaseClient()
                        .sql(R2dbcPostgresqlLeaseRecordDao.INSERT_STATEMENT)
                        .bind("$1", leaseRecord.getLeaseRequestUuid())
                        .bind("$2", leaseRecord.getStartTimeInEpochMilliseconds())
                        .bind("$3", leaseRecord.getDurationInMilliseconds())
                        .bind("$4", leaseRecord.getRelinquished())
                        .fetch()
                        .rowsUpdated()
                        .then()))
                .doOnSuccess(v -> leaseRecord.setRelinquished(TRUE))
                .then(Mono.defer(() -> leaseRecordDao.markAsRelinquished(leaseRecord.getLeaseRequestId())))
                .then(entityTemplate.select(R2dbcLeaseRecord.class).one())
                .as(StepVerifier::create)
                .expectSubscription()
                .consumeNextWith(actualLeaseRecord -> {
                    log.info("Expected {}", leaseRecord);
                    log.info("Actual {}", actualLeaseRecord);

                    assertThat(actualLeaseRecord).isEqualTo(leaseRecord);
                })
                .expectComplete()
                .verify();
    }

    @Test
    void givenNonExistingLeaseRequestId_whenMarkAsRelinquished_shouldEmitEmpty() {
        leaseRecordDao
                .markAsRelinquished(UUID.randomUUID().toString())
                .as(StepVerifier::create)
                .expectSubscription()
                .expectComplete()
                .verify();
    }

    @Test
    void givenMalformedLeaseRequestUuid_whenMarkAsRelinquished_shouldRelinquish() {
        leaseRecordDao
                .markAsRelinquished(UUID.randomUUID().toString())
                .as(StepVerifier::create)
                .expectSubscription()
                .expectComplete()
                .verify();
    }


    @Test
    void givenSetOfActiveLeaseRecords_getActiveLeaseRecords_shouldGetActiveLeaseRecords() {
        Resource dataScript = new ClassPathResource("sample_data.sql");
        entityTemplate
                .getDatabaseClient()
                .inConnection(conn -> executeSqlScript(conn, dataScript))
                .thenMany(leaseRecordDao.getActiveLeaseRecords("library1", 0, 20))
                .as(StepVerifier::create)
                .expectSubscription()
                .expectNextCount(3)
                .expectComplete()
                .verify();
    }

    @Test
    void givenSetOfActiveLeaseRecords_getActiveLeaseRecordsByUserId_shouldGetActiveLeaseRecords() {
        Resource dataScript = new ClassPathResource("sample_data.sql");
        entityTemplate
                .getDatabaseClient()
                .inConnection(conn -> executeSqlScript(conn, dataScript))
                .thenMany(leaseRecordDao.getActiveLeaseRecords("library2", "qbloxland4", 0, 20))
                .as(StepVerifier::create)
                .expectSubscription()
                .expectNextCount(1)
                .expectComplete()
                .verify();
    }

    @Test
    void givenSetOfStaleEBookLeaseRecords_getgetStaleEBookLeaseRecordIds_shouldGetgetStaleEBookLeaseRecordIds() {

        Resource staleEBookLeaseSqlScript = new ClassPathResource("sample_data.sql");

        Set<UUID> expected = Set.of(
                UUID.fromString("61c22813-7faa-4fb4-9aee-2ce132520d9e"),
                UUID.fromString("b9d8ed6b-8bb4-43cd-a3f9-7fed8b8b1d8f"));

        entityTemplate
                .getDatabaseClient()
                .inConnection(conn -> executeSqlScript(conn, staleEBookLeaseSqlScript))
                .thenMany(leaseRecordDao.getStaleEBookLeaseRecordIds())
                .map(UUID::fromString)
                .collect(Collectors.toCollection(HashSet::new))
                .cast(Set.class)
                .as(StepVerifier::create)
                .expectSubscription()
                .consumeNextWith(actualIds -> {
                    log.info("Expected {}", expected);
                    log.info("Actual {}", actualIds);

                    assertThat(actualIds).isEqualTo(expected);
                })
                .expectComplete()
                .verify();

    }
}