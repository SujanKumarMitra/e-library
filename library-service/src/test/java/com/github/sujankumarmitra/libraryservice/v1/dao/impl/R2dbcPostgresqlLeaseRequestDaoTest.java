package com.github.sujankumarmitra.libraryservice.v1.dao.impl;

import com.github.sujankumarmitra.libraryservice.v1.dao.impl.entity.R2dbcBook;
import com.github.sujankumarmitra.libraryservice.v1.dao.impl.entity.R2dbcLeaseRequest;
import com.github.sujankumarmitra.libraryservice.v1.exception.BookNotFoundException;
import com.github.sujankumarmitra.libraryservice.v1.model.LeaseRequest;
import com.github.sujankumarmitra.libraryservice.v1.model.LeaseStatus;
import com.github.sujankumarmitra.libraryservice.v1.util.BookDaoTestUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.*;
import java.util.stream.Collectors;

import static com.github.sujankumarmitra.libraryservice.v1.util.DaoTestUtils.truncateAllTables;
import static com.github.sujankumarmitra.libraryservice.v1.model.LeaseStatus.*;
import static java.util.Collections.shuffle;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author skmitra
 * @since Dec 07/12/21, 2021
 */
@Slf4j
class R2dbcPostgresqlLeaseRequestDaoTest extends AbstractDataR2dbcPostgreSQLContainerDependentTest {

    private R2dbcPostgresqlLeaseRequestDao leaseRequestDao;
    @Autowired
    private R2dbcEntityTemplate entityTemplate;

    @BeforeEach
    void setUp() {
        leaseRequestDao = new R2dbcPostgresqlLeaseRequestDao(entityTemplate);
    }

    @AfterEach
    void tearDown() {
        truncateAllTables(entityTemplate.getDatabaseClient())
                .block();
    }

    @Test
    void givenValidLeaseRequestId_whenGet_shouldGet() {

        R2dbcLeaseRequest leaseRequest = new R2dbcLeaseRequest();

        leaseRequest.setStatus(PENDING);
        leaseRequest.setTimestamp(System.currentTimeMillis());
        leaseRequest.setUserId("user_id");

        BookDaoTestUtils
                .insertDummyBook(entityTemplate.getDatabaseClient())
                .map(R2dbcBook::getUuid)
                .doOnNext(leaseRequest::setBookId)
                .then(entityTemplate.insert(leaseRequest))
                .map(R2dbcLeaseRequest::getId)
                .flatMap(leaseRequestDao::getLeaseRequest)
                .as(StepVerifier::create)
                .expectSubscription()
                .consumeNextWith(actualLeaseRequest -> {
                    log.info("Expected {}", leaseRequest);
                    log.info("Actual {}", actualLeaseRequest);

                    assertThat(actualLeaseRequest).isEqualTo(leaseRequest);
                })
                .expectComplete()
                .verify();

    }

    @Test
    void givenNonExistingLeaseRequestId_whenGet_shouldEmitComplete() {
        leaseRequestDao
                .getLeaseRequest(UUID.randomUUID().toString())
                .as(StepVerifier::create)
                .expectSubscription()
                .expectComplete()
                .verify();

    }

    @Test
    void givenMalformedLeaseRequestUuid_whenGet_shouldEmitComplete() {
        leaseRequestDao
                .getLeaseRequest("malformed_uuid")
                .as(StepVerifier::create)
                .expectSubscription()
                .expectComplete()
                .verify();

    }


    @Test
    void givenListOfPendingLeaseRequests_whenFetchedPendingLeaseRequest_shouldFetch() {
        List<R2dbcLeaseRequest> leaseRequests = new ArrayList<>();

        for (int i = 1; i <= 10; i++) {
            R2dbcLeaseRequest request = new R2dbcLeaseRequest();

            request.setTimestamp(System.currentTimeMillis());
            request.setUserId("user_" + i);
            request.setStatus(EXPIRED);

            leaseRequests.add(request);
        }

        Set<R2dbcLeaseRequest> pendingLeaseRequests = new HashSet<>();

        for (int i = 11; i <= 15; i++) {
            R2dbcLeaseRequest request = new R2dbcLeaseRequest();

            request.setTimestamp(System.currentTimeMillis());
            request.setUserId("user_" + i);
            request.setStatus(PENDING);

            leaseRequests.add(request);
            pendingLeaseRequests.add(request);
        }

        shuffle(leaseRequests);

        BookDaoTestUtils
                .insertDummyBook(entityTemplate.getDatabaseClient())
                .map(R2dbcBook::getUuid)
                .doOnNext(id -> leaseRequests.forEach(request -> request.setBookId(id)))
                .thenMany(Flux.fromIterable(leaseRequests))
                .flatMap(request -> entityTemplate
                        .insert(request)
                        .doOnNext(savedRequest -> request.setId(savedRequest.getUuid())))
                .as(StepVerifier::create)
                .expectSubscription()
                .thenConsumeWhile(req -> true)
                .expectComplete()
                .verify();


        leaseRequestDao
                .getPendingLeaseRequests(0, 5)
                .collect(Collectors.toCollection(HashSet::new))
                .as(StepVerifier::create)
                .expectSubscription()
                .consumeNextWith(actualPendingLeaseRequests -> {
                    log.info("Expected pending count={}", pendingLeaseRequests.size());
                    log.info("Actual pending count={}", actualPendingLeaseRequests.size());

                    log.info("Expected pending leases {}", pendingLeaseRequests);
                    log.info("Actual pending leases {}", actualPendingLeaseRequests);

                    assertThat(actualPendingLeaseRequests).isEqualTo(pendingLeaseRequests);
                })
                .expectComplete()
                .verify();
    }

    @Test
    void givenListOfPendingLeaseRequests_whenFetchedPendingLeaseRequestByUserId_shouldFetch() {
        List<UUID> bookIds = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            BookDaoTestUtils
                    .insertDummyBook(entityTemplate.getDatabaseClient())
                    .map(R2dbcBook::getUuid)
                    .doOnNext(bookIds::add)
                    .block();
        }

        Set<R2dbcLeaseRequest> leaseRequests = new HashSet<>();
        Set<R2dbcLeaseRequest> leaseRequestsForTestUser = new HashSet<>();

        String userId = "userId1";

        for (int i = 0; i < 3; i++) {
            R2dbcLeaseRequest leaseRequest = new R2dbcLeaseRequest();

            leaseRequest.setBookId(bookIds.get(i));
            leaseRequest.setUserId(userId);
            leaseRequest.setStatus(PENDING);
            leaseRequest.setTimestamp(System.currentTimeMillis());

            leaseRequests.add(leaseRequest);
            leaseRequestsForTestUser.add(leaseRequest);
        }

        for (int i = 3; i < 5; i++) {
            R2dbcLeaseRequest leaseRequest = new R2dbcLeaseRequest();

            leaseRequest.setBookId(bookIds.get(i));
            leaseRequest.setUserId("user_id2");
            leaseRequest.setStatus(PENDING);
            leaseRequest.setTimestamp(System.currentTimeMillis());

            leaseRequests.add(leaseRequest);
        }

        Flux.fromIterable(leaseRequests)
                .flatMap(request -> entityTemplate
                        .insert(request)
                        .doOnNext(savedRequest -> request.setId(savedRequest.getUuid())))
                .thenMany(leaseRequestDao.getPendingLeaseRequests(userId, 0, 5))
                .collect(Collectors.toCollection(HashSet::new))
                .as(StepVerifier::create)
                .expectSubscription()
                .consumeNextWith(actualPendingLeaseRequestsForUser -> {
                    log.info("Expected pending count {}", leaseRequestsForTestUser.size());
                    log.info("Actual pending count {}", actualPendingLeaseRequestsForUser.size());

                    log.info("Expected pending {}", leaseRequestsForTestUser);
                    log.info("Actual pending {}", actualPendingLeaseRequestsForUser);
                })
                .expectComplete()
                .verify();
    }

    @Test
    void givenValidLeaseRequest_whenCreate_shouldCreate() {
        R2dbcLeaseRequest leaseRequest = new R2dbcLeaseRequest();

        leaseRequest.setUserId("user_id");
        leaseRequest.setTimestamp(System.currentTimeMillis());
        leaseRequest.setStatus(PENDING);

        BookDaoTestUtils
                .insertDummyBook(entityTemplate.getDatabaseClient())
                .map(R2dbcBook::getUuid)
                .doOnNext(leaseRequest::setBookId)
                .then(leaseRequestDao.createLeaseRequest(leaseRequest))
                .map(UUID::fromString)
                .doOnNext(leaseRequest::setId)
                .then(entityTemplate.select(R2dbcLeaseRequest.class).one())
                .as(StepVerifier::create)
                .expectSubscription()
                .consumeNextWith(savedLeaseRequest -> {
                    log.info("Expected {}", leaseRequest);
                    log.info("Actual {}", savedLeaseRequest);

                    assertThat(savedLeaseRequest).isEqualTo(leaseRequest);
                })
                .expectComplete()
                .verify();
    }

    @Test
    void givenLeaseRequestWithNonExistingBookId_whenCreate_shouldEmitError() {
        R2dbcLeaseRequest leaseRequest = new R2dbcLeaseRequest();

        leaseRequest.setUserId("user_id");
        leaseRequest.setTimestamp(System.currentTimeMillis());
        leaseRequest.setStatus(PENDING);
        leaseRequest.setBookId(UUID.randomUUID());

        leaseRequestDao
                .createLeaseRequest(leaseRequest)
                .as(StepVerifier::create)
                .expectSubscription()
                .expectError(BookNotFoundException.class)
                .verify();
    }

    @Test
    void givenLeaseRequestWithMalformedBookUuid_whenCreate_shouldEmitError() {
        LeaseRequest leaseRequest = new LeaseRequest() {
            @Override
            public String getId() {
                return null;
            }

            @Override
            public String getBookId() {
                return "malformed";
            }

            @Override
            public String getUserId() {
                return "user_id";
            }

            @Override
            public LeaseStatus getStatus() {
                return PENDING;
            }

            @Override
            public Long getTimestamp() {
                return System.currentTimeMillis();
            }
        };


        leaseRequestDao
                .createLeaseRequest(leaseRequest)
                .as(StepVerifier::create)
                .expectSubscription()
                .expectError(BookNotFoundException.class)
                .log()
                .verify();
    }

    @Test
    void givenValidPendingLeaseRequestId_whenDelete_shouldDelete() {
        R2dbcLeaseRequest leaseRequest = new R2dbcLeaseRequest();

        leaseRequest.setStatus(PENDING);
        leaseRequest.setTimestamp(System.currentTimeMillis());
        leaseRequest.setUserId("user_id");

        BookDaoTestUtils
                .insertDummyBook(entityTemplate.getDatabaseClient())
                .map(R2dbcBook::getUuid)
                .doOnNext(leaseRequest::setBookId)
                .then(entityTemplate.insert(leaseRequest))
                .map(R2dbcLeaseRequest::getId)
                .flatMap(leaseRequestDao::deletePendingLeaseRequest)
                .then(entityTemplate
                        .select(R2dbcLeaseRequest.class)
                        .count())
                .as(StepVerifier::create)
                .expectSubscription()
                .expectNext(0L)
                .expectComplete()
                .verify();
    }

    @Test
    void givenValidLeaseRequestNotInPendingState_whenDelete_shouldNotDelete() {
        R2dbcLeaseRequest leaseRequest = new R2dbcLeaseRequest();

        leaseRequest.setStatus(REJECTED);
        leaseRequest.setTimestamp(System.currentTimeMillis());
        leaseRequest.setUserId("user_id");

        BookDaoTestUtils
                .insertDummyBook(entityTemplate.getDatabaseClient())
                .map(R2dbcBook::getUuid)
                .doOnNext(leaseRequest::setBookId)
                .then(entityTemplate.insert(leaseRequest))
                .map(R2dbcLeaseRequest::getId)
                .flatMap(leaseRequestDao::deletePendingLeaseRequest)
                .then(entityTemplate
                        .select(R2dbcLeaseRequest.class)
                        .count())
                .as(StepVerifier::create)
                .expectSubscription()
                .expectNext(1L)
                .expectComplete()
                .verify();
    }

    @Test
    void givenNonExistingLeaseRequestId_whenDelete_shouldEmitComplete() {
        leaseRequestDao
                .deletePendingLeaseRequest(UUID.randomUUID().toString())
                .as(StepVerifier::create)
                .expectSubscription()
                .expectComplete()
                .verify();
    }

    @Test
    void givenMalformedLeaseRequestUuid_whenDelete_shouldEmitComplete() {
        leaseRequestDao
                .deletePendingLeaseRequest("malformed")
                .as(StepVerifier::create)
                .expectSubscription()
                .expectComplete()
                .verify();
    }

    @Test
    void givenValidLeaseRequestId_whenUpdateStatus_shouldSetStatus() {
        R2dbcLeaseRequest leaseRequest = new R2dbcLeaseRequest();
        leaseRequest.setStatus(PENDING);
        leaseRequest.setTimestamp(System.currentTimeMillis());
        leaseRequest.setUserId("user_id");

        BookDaoTestUtils
                .insertDummyBook(entityTemplate.getDatabaseClient())
                .map(R2dbcBook::getUuid)
                .doOnNext(leaseRequest::setBookId)
                .then(entityTemplate.insert(leaseRequest))
                .doOnNext(savedLeaseRequest -> leaseRequest.setId(savedLeaseRequest.getUuid()))
                .doOnSuccess(s -> leaseRequest.setStatus(EXPIRED))
                .then(Mono.defer(() -> leaseRequestDao.setLeaseStatus(leaseRequest.getId(), leaseRequest.getStatus())))
                .then(entityTemplate.select(R2dbcLeaseRequest.class).one())
                .as(StepVerifier::create)
                .expectSubscription()
                .consumeNextWith(updatedLeaseRequest -> {
                    log.info("Expected {}", leaseRequest);
                    log.info("Actual {}", updatedLeaseRequest);

                    assertThat(updatedLeaseRequest).isEqualTo(leaseRequest);
                })
                .expectComplete()
                .verify();

    }

    @Test
    void givenNonExistingLeaseRequestId_whenUpdateStatus_shouldEmitComplete() {
        leaseRequestDao
                .setLeaseStatus(UUID.randomUUID().toString(), EXPIRED)
                .as(StepVerifier::create)
                .expectSubscription()
                .expectComplete()
                .verify();
    }

    @Test
    void givenMalformedLeaseRequestUuid_whenUpdateStatus_shouldEmitComplete() {
        leaseRequestDao
                .setLeaseStatus("malformed", EXPIRED)
                .as(StepVerifier::create)
                .expectSubscription()
                .expectComplete()
                .verify();
    }


}