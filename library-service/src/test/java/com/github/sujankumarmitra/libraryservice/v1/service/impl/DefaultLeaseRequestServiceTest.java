package com.github.sujankumarmitra.libraryservice.v1.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.sujankumarmitra.libraryservice.v1.config.DefaultPagingProperties;
import com.github.sujankumarmitra.libraryservice.v1.config.PagingProperties;
import com.github.sujankumarmitra.libraryservice.v1.dao.LeaseRecordDao;
import com.github.sujankumarmitra.libraryservice.v1.dao.LeaseRequestDao;
import com.github.sujankumarmitra.libraryservice.v1.dao.LibrarianDao;
import com.github.sujankumarmitra.libraryservice.v1.dao.RejectedLeaseDao;
import com.github.sujankumarmitra.libraryservice.v1.dao.impl.entity.R2dbcLeaseRequest;
import com.github.sujankumarmitra.libraryservice.v1.dao.impl.entity.R2dbcPhysicalBook;
import com.github.sujankumarmitra.libraryservice.v1.exception.InsufficientCopiesAvailableException;
import com.github.sujankumarmitra.libraryservice.v1.exception.LeaseRequestAlreadyHandledException;
import com.github.sujankumarmitra.libraryservice.v1.model.impl.DefaultAcceptedLease;
import com.github.sujankumarmitra.libraryservice.v1.service.BookService;
import com.github.sujankumarmitra.libraryservice.v1.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.UUID;

import static com.github.sujankumarmitra.libraryservice.v1.model.LeaseStatus.ACCEPTED;
import static com.github.sujankumarmitra.libraryservice.v1.model.LeaseStatus.PENDING;
import static org.mockito.ArgumentMatchers.any;

/**
 * @author skmitra
 * @since Dec 07/12/21, 2021
 */
@ExtendWith(MockitoExtension.class)
class DefaultLeaseRequestServiceTest {

    private DefaultLeaseRequestService leaseRequestService;
    @Mock
    private LeaseRequestDao leaseRequestDao;
    @Mock
    private LeaseRecordDao leaseRecordDao;
    @Mock
    private RejectedLeaseDao rejectedLeaseDao;
    @Mock
    private BookService bookService;
    @Mock
    private NotificationService notificationService;
    @Mock
    private LibrarianDao librarianDao;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final PagingProperties pagingProperties = new DefaultPagingProperties();


    @BeforeEach
    void setUp() {
        leaseRequestService = new DefaultLeaseRequestService(
                leaseRequestDao,
                leaseRecordDao,
                rejectedLeaseDao,
                librarianDao,
                bookService,
                notificationService,
                objectMapper,
                pagingProperties
        );
    }


    @Test
    void givenValidEnvironment_whenAccept_shouldAccept() {

        UUID validLeaseRequestId = UUID.randomUUID();

        R2dbcLeaseRequest leaseRequest = new R2dbcLeaseRequest();

        leaseRequest.setId(validLeaseRequestId);
        leaseRequest.setTimestamp(System.currentTimeMillis() - Duration.ofHours(2).toMillis());
        leaseRequest.setStatus(PENDING);
        leaseRequest.setUserId("user_id");

        Mockito.doReturn(Mono.fromSupplier(() -> leaseRequest))
                .when(leaseRequestDao).getLeaseRequest(validLeaseRequestId.toString());

        Mockito.doReturn(Mono.empty())
                .when(bookService).onLeaseAccept(any());

        Mockito.doReturn(Mono.empty())
                .when(leaseRecordDao).createLeaseRecord(any());

        Mockito.doReturn(Mono.empty())
                .when(leaseRequestDao).setLeaseStatus(validLeaseRequestId.toString(), ACCEPTED);

        Mockito.doReturn(Mono.empty())
                .when(notificationService).sendNotification(any());


        DefaultAcceptedLease acceptedLease = new DefaultAcceptedLease();
        acceptedLease.setLeaseRequestId(validLeaseRequestId.toString());
        acceptedLease.setStartTimeInEpochMilliseconds(System.currentTimeMillis());
        acceptedLease.setDurationInMilliseconds(System.currentTimeMillis() + Duration.ofHours(2).toMillis());

        leaseRequestService
                .acceptLeaseRequest(acceptedLease)
                .log()
                .as(StepVerifier::create)
                .expectSubscription()
                .expectComplete()
                .verify();

    }

    @Test
    void givenNotificationServiceThatEmitsError_whenAccept_shouldNotPropagateErrorSignal() {

        UUID validLeaseRequestId = UUID.randomUUID();

        R2dbcLeaseRequest leaseRequest = new R2dbcLeaseRequest();

        leaseRequest.setId(validLeaseRequestId);
        leaseRequest.setTimestamp(System.currentTimeMillis() - Duration.ofHours(2).toMillis());
        leaseRequest.setStatus(PENDING);
        leaseRequest.setUserId("user_id");

        Mockito.doReturn(Mono.fromSupplier(() -> leaseRequest))
                .when(leaseRequestDao).getLeaseRequest(validLeaseRequestId.toString());

        Mockito.doReturn(Mono.empty())
                .when(bookService).onLeaseAccept(any());

        Mockito.doReturn(Mono.empty())
                .when(leaseRecordDao).createLeaseRecord(any());

        Mockito.doReturn(Mono.empty())
                .when(leaseRequestDao).setLeaseStatus(validLeaseRequestId.toString(), ACCEPTED);

        Mockito.doReturn(Mono.error(new RuntimeException()))
                .when(notificationService).sendNotification(any());


        DefaultAcceptedLease acceptedLease = new DefaultAcceptedLease();
        acceptedLease.setLeaseRequestId(validLeaseRequestId.toString());
        acceptedLease.setStartTimeInEpochMilliseconds(System.currentTimeMillis());
        acceptedLease.setDurationInMilliseconds(System.currentTimeMillis() + Duration.ofHours(2).toMillis());

        leaseRequestService
                .acceptLeaseRequest(acceptedLease)
                .log()
                .as(StepVerifier::create)
                .expectSubscription()
                .expectComplete()
                .verify();

    }

    @Test
    void givenLeaseRequestWhichIsAlreadyHandled_whenAccept_shouldEmitError() {

        UUID validLeaseRequestId = UUID.randomUUID();

        R2dbcLeaseRequest leaseRequest = new R2dbcLeaseRequest();

        leaseRequest.setId(validLeaseRequestId);
        leaseRequest.setTimestamp(System.currentTimeMillis() - Duration.ofHours(2).toMillis());
        leaseRequest.setStatus(ACCEPTED);
        leaseRequest.setUserId("user_id");

        Mockito.doReturn(Mono.fromSupplier(() -> leaseRequest))
                .when(leaseRequestDao).getLeaseRequest(validLeaseRequestId.toString());

        DefaultAcceptedLease acceptedLease = new DefaultAcceptedLease();
        acceptedLease.setLeaseRequestId(validLeaseRequestId.toString());
        acceptedLease.setStartTimeInEpochMilliseconds(System.currentTimeMillis());
        acceptedLease.setDurationInMilliseconds(System.currentTimeMillis() + Duration.ofHours(2).toMillis());

        leaseRequestService
                .acceptLeaseRequest(acceptedLease)
                .log()
                .as(StepVerifier::create)
                .expectSubscription()
                .expectError(LeaseRequestAlreadyHandledException.class)
                .verify();


    }

    @Test
    void givenBookServiceWhichEmitsErrorWhenHandleLeaseAccept_shouldEmitErrorSignalReturnedByBookService() {
        UUID validLeaseRequestId = UUID.randomUUID();

        R2dbcLeaseRequest leaseRequest = new R2dbcLeaseRequest();

        leaseRequest.setId(validLeaseRequestId);
        leaseRequest.setTimestamp(System.currentTimeMillis() - Duration.ofHours(2).toMillis());
        leaseRequest.setStatus(PENDING);
        leaseRequest.setUserId("user_id");

        Mockito.doReturn(Mono.fromSupplier(() -> leaseRequest))
                .when(leaseRequestDao).getLeaseRequest(validLeaseRequestId.toString());

        Mockito.doReturn(Mono.error(new InsufficientCopiesAvailableException("bookId")))
                .when(bookService).onLeaseAccept(any());

        Mockito.doReturn(Mono.empty())
                .when(notificationService).sendNotification(any());

        DefaultAcceptedLease acceptedLease = new DefaultAcceptedLease();
        acceptedLease.setLeaseRequestId(validLeaseRequestId.toString());
        acceptedLease.setStartTimeInEpochMilliseconds(System.currentTimeMillis());
        acceptedLease.setDurationInMilliseconds(System.currentTimeMillis() + Duration.ofHours(2).toMillis());

        leaseRequestService
                .acceptLeaseRequest(acceptedLease)
                .log()
                .as(StepVerifier::create)
                .expectSubscription()
                .expectError(InsufficientCopiesAvailableException.class)
                .verify();
    }

    @Test
    void givenPhysicalBookWithNoCopiesAvailable_whenCreateLease_shouldEmitError() {
        UUID validPhysicalBookId = UUID.randomUUID();


        R2dbcPhysicalBook physicalBook = new R2dbcPhysicalBook();
        physicalBook.setLibraryId("library1");
        physicalBook.setId(validPhysicalBookId);
        physicalBook.setCopiesAvailable(0L);

        Mockito.doReturn(Mono.fromSupplier(() -> physicalBook))
                .when(bookService).getBook(validPhysicalBookId.toString());

        R2dbcLeaseRequest leaseRequest = new R2dbcLeaseRequest();
        leaseRequest.setLibraryId("library1");
        leaseRequest.setBookId(validPhysicalBookId);


        leaseRequestService
                .createLeaseRequest(leaseRequest)
                .as(StepVerifier::create)
                .expectSubscription()
                .expectError(InsufficientCopiesAvailableException.class)
                .verify();
    }
}