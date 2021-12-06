package com.github.sujankumarmitra.libraryservice.v1.dao.impl;

import com.github.sujankumarmitra.libraryservice.v1.dao.impl.entity.R2dbcLeaseRequest;
import com.github.sujankumarmitra.libraryservice.v1.dao.impl.entity.R2dbcRejectedLease;
import com.github.sujankumarmitra.libraryservice.v1.exception.LeaseRequestNotFoundException;
import com.github.sujankumarmitra.libraryservice.v1.model.RejectedLease;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author skmitra
 * @since Dec 06/12/21, 2021
 */
@Slf4j
class R2dbcPostgresqlRejectedLeaseDaoTest extends AbstractDataR2dbcPostgreSQLContainerDependentTest {

    @Autowired
    private R2dbcEntityTemplate entityTemplate = null;
    private R2dbcPostgresqlRejectedLeaseDao rejectedLeaseDao;

    @BeforeEach
    void setUp() {
        rejectedLeaseDao = new R2dbcPostgresqlRejectedLeaseDao(entityTemplate);
    }

    @AfterEach
    void tearDown() {

        entityTemplate
                .getDatabaseClient()
                .sql("DELETE FROM rejected_lease_requests")
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
                .sql("DELETE FROM books")
                .fetch()
                .rowsUpdated()
                .block();
    }

    @Test
    void givenValidRejectedLease_whenInsert_shouldInsert() {
        R2dbcRejectedLease rejectedLease = new R2dbcRejectedLease();
        rejectedLease.setReasonPhrase("rejected");

        BookDaoTestUtils
                .insertDummyBook(entityTemplate.getDatabaseClient())
                .flatMap(book -> LeaseRequestDaoTestUtils.insertLeaseRequest(entityTemplate.getDatabaseClient(), book.getUuid()))
                .map(R2dbcLeaseRequest::getUuid)
                .doOnNext(rejectedLease::setLeaseRequestId)
                .then(rejectedLeaseDao.createRejectedLease(rejectedLease))
                .then(Mono.defer(() -> entityTemplate
                        .select(R2dbcRejectedLease.class)
                        .one()))
                .as(StepVerifier::create)
                .expectSubscription()
                .consumeNextWith(actualRejectedLease -> {
                    log.info("Expected {}", rejectedLease);
                    log.info("Actual {}", actualRejectedLease);
                    assertThat(actualRejectedLease).isEqualTo(rejectedLease);
                })
                .expectComplete()
                .verify();
    }

    @Test
    void givenNonExistingLeaseRequest_whenCreateRejectedLease_shouldEmitError() {
        R2dbcRejectedLease rejectedLease = new R2dbcRejectedLease();
        rejectedLease.setLeaseRequestId(UUID.randomUUID());
        rejectedLease.setReasonPhrase("does not matter");

        rejectedLeaseDao
                .createRejectedLease(rejectedLease)
                .as(StepVerifier::create)
                .expectSubscription()
                .expectErrorSatisfies(th -> {
                    log.info("", th);
                    assertThat(th)
                            .isExactlyInstanceOf(LeaseRequestNotFoundException.class);
                })
                .verify();
    }

    @Test
    void givenMalformedLeaseRequestUuid_whenCreateRejectedLease_shouldEmitError() {
        RejectedLease rejectedLease = new RejectedLease() {
            @Override
            public String getLeaseRequestId() {
                return "malformed";
            }

            @Override
            public String getReasonPhrase() {
                return "null";
            }
        };

        rejectedLeaseDao
                .createRejectedLease(rejectedLease)
                .as(StepVerifier::create)
                .expectSubscription()
                .expectErrorSatisfies(th -> {
                    log.info("", th);
                    assertThat(th)
                            .isExactlyInstanceOf(LeaseRequestNotFoundException.class);
                })
                .verify();
    }


    @Test
    void givenValidLeaseRequestId_whenGet_shouldGetRejectedLeaseRequest() {
        R2dbcRejectedLease rejectedLease = new R2dbcRejectedLease();
        rejectedLease.setReasonPhrase("rejected");

        BookDaoTestUtils
                .insertDummyBook(entityTemplate.getDatabaseClient())
                .flatMap(book -> LeaseRequestDaoTestUtils.insertLeaseRequest(entityTemplate.getDatabaseClient(), book.getUuid()))
                .map(R2dbcLeaseRequest::getUuid)
                .doOnNext(rejectedLease::setLeaseRequestId)
                .then(Mono.defer(() ->
                        entityTemplate
                                .getDatabaseClient()
                                .sql(R2dbcPostgresqlRejectedLeaseDao.INSERT_STATEMENT)
                                .bind("$1", rejectedLease.getLeaseRequestUuid())
                                .bind("$2", rejectedLease.getReasonPhrase())
                                .fetch()
                                .rowsUpdated()
                                .thenReturn(rejectedLease.getLeaseRequestId())))
                .flatMap(id -> rejectedLeaseDao.getRejectedLease(id))
                .as(StepVerifier::create)
                .expectSubscription()
                .consumeNextWith(actualRejectedLease -> {
                    log.info("Expected {}", rejectedLease);
                    log.info("Actual {}", actualRejectedLease);

                    assertThat(actualRejectedLease).isEqualTo(rejectedLease);
                })
                .expectComplete()
                .verify();
    }

    @Test
    void givenNonExistingLeaseRequestId_whenGet_shouldEmitEmpty() {
        rejectedLeaseDao
                .getRejectedLease(UUID.randomUUID().toString())
                .as(StepVerifier::create)
                .expectSubscription()
                .expectComplete()
                .verify();
    }

    @Test
    void givenMalformedLeaseRequestId_whenGet_shouldEmitEmpty() {
        rejectedLeaseDao
                .getRejectedLease("malformed")
                .as(StepVerifier::create)
                .expectSubscription()
                .expectComplete()
                .verify();
    }

}