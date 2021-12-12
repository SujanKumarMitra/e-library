package com.github.sujankumarmitra.libraryservice.v1.service.impl;

import com.github.sujankumarmitra.libraryservice.v1.config.DefaultPagingProperties;
import com.github.sujankumarmitra.libraryservice.v1.config.PagingProperties;
import com.github.sujankumarmitra.libraryservice.v1.dao.BookSearchDao;
import com.github.sujankumarmitra.libraryservice.v1.dao.EBookDao;
import com.github.sujankumarmitra.libraryservice.v1.dao.LeaseRequestDao;
import com.github.sujankumarmitra.libraryservice.v1.dao.PhysicalBookDao;
import com.github.sujankumarmitra.libraryservice.v1.dao.impl.entity.R2dbcEBook;
import com.github.sujankumarmitra.libraryservice.v1.dao.impl.entity.R2dbcLeaseRequest;
import com.github.sujankumarmitra.libraryservice.v1.dao.impl.entity.R2dbcPhysicalBook;
import com.github.sujankumarmitra.libraryservice.v1.exception.InsufficientCopiesAvailableException;
import com.github.sujankumarmitra.libraryservice.v1.model.AcceptedLease;
import com.github.sujankumarmitra.libraryservice.v1.model.impl.DefaultAcceptedLease;
import com.github.sujankumarmitra.libraryservice.v1.service.EBookPermissionService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.UUID;

import static com.github.sujankumarmitra.libraryservice.v1.model.LeaseStatus.PENDING;
import static org.mockito.ArgumentMatchers.any;

/**
 * @author skmitra
 * @since Dec 07/12/21, 2021
 */
@Slf4j
@ExtendWith(MockitoExtension.class)
class DefaultBookServiceTest {

    private DefaultBookService bookService;
    @Mock
    private PhysicalBookDao physicalBookDao;
    @Mock
    private EBookDao eBookDao;
    @Mock
    private LeaseRequestDao leaseRequestDao;
    @Mock
    private BookSearchDao bookSearchDao;
    @Mock
    private EBookPermissionService eBookPermissionService;
    private final PagingProperties pagingProperties = new DefaultPagingProperties();

    @BeforeEach
    void setUp() {

        bookService = new DefaultBookService(
                physicalBookDao,
                eBookDao,
                leaseRequestDao,
                bookSearchDao,
                eBookPermissionService,
                pagingProperties);
    }

    @Test
    void givenValidPhysicalBookId_whenHandleLeaseRequest_shouldHandleLeaseRequest() {

        UUID validLeaseRequestId = UUID.randomUUID();
        UUID validPhysicalBookId = UUID.randomUUID();

        R2dbcLeaseRequest leaseRequest = new R2dbcLeaseRequest();

        leaseRequest.setId(validLeaseRequestId);
        leaseRequest.setBookId(validPhysicalBookId);
        leaseRequest.setStatus(PENDING);
        leaseRequest.setTimestamp(System.currentTimeMillis() - Duration.ofDays(2).toMillis());

        Mockito.doReturn(Mono.fromSupplier(() -> leaseRequest))
                .when(leaseRequestDao).getLeaseRequest(validLeaseRequestId.toString());

        R2dbcPhysicalBook physicalBook = new R2dbcPhysicalBook();

        physicalBook.setId(validPhysicalBookId);
        physicalBook.setCopiesAvailable(10L);

        Mockito.doReturn(Mono.empty())
                .when(physicalBookDao).decrementCopiesAvailable(validPhysicalBookId.toString());

        Mockito.doReturn(Mono.fromSupplier(() -> physicalBook))
                .when(physicalBookDao).getBook(validPhysicalBookId.toString());

        AcceptedLease acceptedLease = buildAcceptedLease(validLeaseRequestId);

        bookService.onLeaseAccept(acceptedLease)
                .as(StepVerifier::create)
                .expectSubscription()
                .expectComplete()
                .verify();

    }

    @Test
    void givenValidEBookId_whenHandleLeaseRequest_shouldHandleLeaseRequest() {

        UUID validLeaseRequestId = UUID.randomUUID();
        UUID validEBookId = UUID.randomUUID();

        R2dbcLeaseRequest leaseRequest = new R2dbcLeaseRequest();

        leaseRequest.setId(validLeaseRequestId);
        leaseRequest.setBookId(validEBookId);
        leaseRequest.setStatus(PENDING);
        leaseRequest.setTimestamp(System.currentTimeMillis() - Duration.ofDays(2).toMillis());

        Mockito.doReturn(Mono.fromSupplier(() -> leaseRequest))
                .when(leaseRequestDao).getLeaseRequest(validLeaseRequestId.toString());

        R2dbcEBook eBook = new R2dbcEBook();

        eBook.setId(validEBookId);

        Mockito.doReturn(Mono.empty())
                .when(physicalBookDao).getBook(any());

        Mockito.doReturn(Mono.fromSupplier(() -> eBook))
                .when(eBookDao).getBook(validEBookId.toString());

//        Mockito.doReturn(Mono.empty())
//                .when(eBookPermissionService).assignPermission(any());

        bookService.setScheduler(Schedulers.immediate());

        AcceptedLease acceptedLease = buildAcceptedLease(validLeaseRequestId);

        bookService.onLeaseAccept(acceptedLease)
                .as(StepVerifier::create)
                .expectSubscription()
                .expectComplete()
                .verify();

    }

    @Test
    void givenPhysicalBookWithNoCopiesAvailable_whenHandleLease_shouldEmitError() {
        UUID validLeaseRequestId = UUID.randomUUID();
        UUID validPhysicalBookId = UUID.randomUUID();

        R2dbcLeaseRequest leaseRequest = new R2dbcLeaseRequest();

        leaseRequest.setId(validLeaseRequestId);
        leaseRequest.setBookId(validPhysicalBookId);
        leaseRequest.setStatus(PENDING);
        leaseRequest.setTimestamp(System.currentTimeMillis() - Duration.ofDays(2).toMillis());

        Mockito.doReturn(Mono.fromSupplier(() -> leaseRequest))
                .when(leaseRequestDao).getLeaseRequest(validLeaseRequestId.toString());

        R2dbcPhysicalBook physicalBook = new R2dbcPhysicalBook();

        physicalBook.setId(validPhysicalBookId);
        physicalBook.setCopiesAvailable(0L);

        Mockito.doReturn(Mono.fromSupplier(() -> physicalBook))
                .when(physicalBookDao).getBook(validPhysicalBookId.toString());

        AcceptedLease acceptedLease = buildAcceptedLease(validLeaseRequestId);

        bookService.onLeaseAccept(acceptedLease)
                .as(StepVerifier::create)
                .expectSubscription()
                .expectError(InsufficientCopiesAvailableException.class)
                .verify();

    }

    @Test
    @Disabled // need clarification
    void givenValidEBook_whenEBookPermissionServiceEmitsError_shouldEmitError() {
        UUID validLeaseRequestId = UUID.randomUUID();
        UUID validEBookId = UUID.randomUUID();

        R2dbcLeaseRequest leaseRequest = new R2dbcLeaseRequest();

        leaseRequest.setId(validLeaseRequestId);
        leaseRequest.setBookId(validEBookId);
        leaseRequest.setStatus(PENDING);
        leaseRequest.setTimestamp(System.currentTimeMillis() - Duration.ofDays(2).toMillis());

        Mockito.doReturn(Mono.fromSupplier(() -> leaseRequest))
                .when(leaseRequestDao).getLeaseRequest(validLeaseRequestId.toString());

        R2dbcEBook eBook = new R2dbcEBook();
        eBook.setId(validEBookId);

        Mockito.doReturn(Mono.empty())
                .when(physicalBookDao).getBook(any());

        Mockito.doReturn(Mono.fromSupplier(() -> eBook))
                .when(eBookDao).getBook(validEBookId.toString());

        Mockito.doReturn(Mono.error(new RuntimeException()))
                .when(eBookPermissionService).assignPermission(any());

        AcceptedLease acceptedLease = buildAcceptedLease(validLeaseRequestId);

        bookService.onLeaseAccept(acceptedLease)
                .as(StepVerifier::create)
                .expectSubscription()
                .expectError()
                .verify();

    }

    private AcceptedLease buildAcceptedLease(UUID validLeaseRequestId) {
        DefaultAcceptedLease acceptedLease = new DefaultAcceptedLease();
        acceptedLease.setLeaseRequestId(validLeaseRequestId.toString());
        acceptedLease.setStartTimeInEpochMilliseconds(System.currentTimeMillis());
        acceptedLease.setDurationInMilliseconds(System.currentTimeMillis() + Duration.ofDays(180).toMillis());
        return acceptedLease;
    }
}