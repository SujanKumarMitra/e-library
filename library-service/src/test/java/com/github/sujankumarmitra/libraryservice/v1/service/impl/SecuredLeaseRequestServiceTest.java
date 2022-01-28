package com.github.sujankumarmitra.libraryservice.v1.service.impl;

import com.github.sujankumarmitra.libraryservice.LazySpringBootTest;
import com.github.sujankumarmitra.libraryservice.v1.dao.LeaseRequestDao;
import com.github.sujankumarmitra.libraryservice.v1.dao.impl.entity.R2dbcLeaseRequest;
import com.github.sujankumarmitra.libraryservice.v1.dao.impl.entity.R2dbcRejectedLease;
import com.github.sujankumarmitra.libraryservice.v1.model.LeaseRequest;
import com.github.sujankumarmitra.libraryservice.v1.model.impl.DefaultAcceptedLease;
import com.github.sujankumarmitra.libraryservice.v1.service.LeaseRequestService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static com.github.sujankumarmitra.libraryservice.v1.security.SecurityConstants.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;

/**
 * @author skmitra
 * @since Jan 28/01/22, 2022
 */
@Slf4j
@LazySpringBootTest
class SecuredLeaseRequestServiceTest {

    @Autowired
    private LeaseRequestService securedLeaseRequestService;
    @MockBean
    private DefaultLeaseRequestService mockLeaseRequestService;
    @MockBean
    private LeaseRequestDao mockLeaseRequestDao;

    @Test
    void shouldInjectBeans() {
        assertNotNull(securedLeaseRequestService);
    }


    @Test
    @WithMockUser(authorities = "library1:" + ROLE_LIBRARIAN)
    void givenLibraryUser_whenGetPendingLeaseRequests_shouldGet() {
        Mockito.doReturn(Flux.empty())
                .when(mockLeaseRequestService)
                .getPendingLeaseRequests(eq("library1"), anyInt());


        securedLeaseRequestService
                .getPendingLeaseRequests("library1", 0)
                .as(StepVerifier::create)
                .expectSubscription()
                .expectComplete()
                .verify();

    }

    @Test
    @WithMockUser(authorities = "library2:" + ROLE_LIBRARIAN)
    void givenUserWithNoAuthoritiesOnGivenLibrary_whenGetPendingLeaseRequests_shouldEmitError() {
        Mockito.doReturn(Flux.empty())
                .when(mockLeaseRequestService)
                .getPendingLeaseRequests(eq("library1"), anyInt());


        securedLeaseRequestService
                .getPendingLeaseRequests("library1", 0)
                .as(StepVerifier::create)
                .expectSubscription()
                .expectError(AccessDeniedException.class)
                .verify();

    }

    @Test
    @WithMockUser(username = "user1", authorities = "library1:" + ROLE_STUDENT)
    void givenStudentUserOfGivenLibrary_whenGetPendingLeaseRequestsForALibrary_shouldGet() {
        Mockito.doReturn(Flux.empty())
                .when(mockLeaseRequestService)
                .getPendingLeaseRequests(eq("library1"), eq("user1"), anyInt());

        securedLeaseRequestService
                .getPendingLeaseRequests("library1", "user1", 0)
                .as(StepVerifier::create)
                .expectSubscription()
                .expectComplete()
                .verify();
    }

    @Test
    @WithMockUser(username = "user1", authorities = "library2:" + ROLE_STUDENT)
    void givenStudentUserOfDifferentLibrary_whenGetPendingLeaseRequestsForALibrary_shouldEmitError() {

        securedLeaseRequestService
                .getPendingLeaseRequests("library1", "user1", 0)
                .as(StepVerifier::create)
                .expectSubscription()
                .expectError(AccessDeniedException.class)
                .verify();
    }

    @Test
    @WithMockUser(username = "user1", authorities = "library1:" + ROLE_STUDENT)
    void shouldNotAllowAccessToOtherStudentsLeaseRequests() {

        securedLeaseRequestService
                .getPendingLeaseRequests("library1", "user2", 0)
                .as(StepVerifier::create)
                .expectSubscription()
                .expectError(AccessDeniedException.class)
                .verify();
    }

    @Test
    @WithMockUser(authorities = "library1:" + ROLE_LIBRARIAN)
    void shouldAllowLeaseAccessOfAGivenLibraryIfUserIsLibrarian() {
        Mockito.doReturn(Flux.empty())
                .when(mockLeaseRequestService).getPendingLeaseRequests(any(), any(), anyInt());

        Flux<LeaseRequest> leasesOfUser1 = securedLeaseRequestService
                .getPendingLeaseRequests("library1", "user1", 0);

        Flux<LeaseRequest> leasesOfUser2 = securedLeaseRequestService
                .getPendingLeaseRequests("library1", "user2", 0);

        Flux.merge(leasesOfUser1, leasesOfUser2)
                .as(StepVerifier::create)
                .expectSubscription()
                .expectComplete()
                .verify();

    }

    @Test
    @WithMockUser(authorities = "library2:" + ROLE_LIBRARIAN)
    void shouldNotAllowLeaseAccessOfADifferentLibraryIfUserIsLibrarian() {
        securedLeaseRequestService
                .getPendingLeaseRequests("library1", "user1", 0)
                .as(StepVerifier::create)
                .expectSubscription()
                .expectError(AccessDeniedException.class)
                .verify();
    }

    @Test
    @WithMockUser(username = "user1", authorities = "library1:" + ROLE_TEACHER)
    void givenTeacherUser_whenCancelOwnLeaseRequest_shouldAllowCancelLeaseRequest() {
        testSuccessfulCancelLeaseRequest();
    }

    @Test
    @WithMockUser(username = "user1", authorities = "library1:" + ROLE_STUDENT)
    void givenStudentUser_whenCancelOwnLeaseRequest_shouldAllowCancelLeaseRequest() {
        testSuccessfulCancelLeaseRequest();
    }


    @Test
    @WithMockUser(username = "user2", authorities = "library1" + ROLE_STUDENT)
    void shouldNotAllowCancellingOtherStudentsLeaseRequest() {
        testFailureCancelLeaseRequest("library1");
    }

    @Test
    @WithMockUser(username = "user2", authorities = "library1" + ROLE_TEACHER)
    void shouldNotAllowCancellingOtherTeachersLeaseRequest() {
        testFailureCancelLeaseRequest("library1");
    }

    @Test
    @WithMockUser(username = "user1", authorities = "library1" + ROLE_TEACHER)
    void shouldNotAllowCancellingLeaseRequestOfDifferentLibrary() {
        testFailureCancelLeaseRequest("library2");
    }

    private void testFailureCancelLeaseRequest(String libraryId) {
        R2dbcLeaseRequest request = new R2dbcLeaseRequest();

        request.setUserId("user1");
        request.setLibraryId(libraryId);

        Mockito.doReturn(Mono.just(request))
                .when(mockLeaseRequestDao)
                .getLeaseRequest("lease1");

        Mockito.doReturn(Mono.empty())
                .when(mockLeaseRequestService)
                .cancelLeaseRequest(any());

        securedLeaseRequestService
                .cancelLeaseRequest("lease1")
                .as(StepVerifier::create)
                .expectSubscription()
                .expectError(AccessDeniedException.class)
                .verify();
    }

    private void testSuccessfulCancelLeaseRequest() {
        R2dbcLeaseRequest request = new R2dbcLeaseRequest();

        request.setUserId("user1");
        request.setLibraryId("library1");

        Mockito.doReturn(Mono.just(request))
                .when(mockLeaseRequestDao)
                .getLeaseRequest("lease1");

        Mockito.doReturn(Mono.empty())
                .when(mockLeaseRequestService)
                .cancelLeaseRequest(any());

        securedLeaseRequestService
                .cancelLeaseRequest("lease1")
                .as(StepVerifier::create)
                .expectSubscription()
                .expectComplete()
                .verify();
    }

    @Test
    @WithMockUser(username = "user1", authorities = "library1:" + ROLE_STUDENT)
    void givenStudentUser_whenCreateLeaseRequest_shouldCreateLeaseRequest() {
        testSuccessfulCreateLeaseRequest();
    }

    @Test
    @WithMockUser(username = "user1", authorities = "library1:" + ROLE_TEACHER)
    void givenTeacherUser_whenCreateLeaseRequest_shouldCreateLeaseRequest() {
        testSuccessfulCreateLeaseRequest();
    }

    @Test
    @WithMockUser(username = "user1", authorities = "library1:" + ROLE_TEACHER)
    void givenUserWithNoAuthorityOnGivenLibrary_whenCreateLeaseRequest_shouldEmitError() {
        testFailureInCreateLeaseRequest("user1", "library2");
    }

    @Test
    @WithMockUser(username = "user1", authorities = "library1:" + ROLE_TEACHER)
    void givenLeaseRequestWithDifferentUserId_whenCreateLeaseRequest_shouldEmitError() {
        testFailureInCreateLeaseRequest("user2", "library1");
    }

    private void testFailureInCreateLeaseRequest(String userId, String libraryId) {
        Mockito.doReturn(Mono.just("leaseRequestId"))
                .when(mockLeaseRequestService).createLeaseRequest(any());

        R2dbcLeaseRequest leaseRequest = new R2dbcLeaseRequest();
        leaseRequest.setUserId(userId);
        leaseRequest.setLibraryId(libraryId);

        securedLeaseRequestService
                .createLeaseRequest(leaseRequest)
                .as(StepVerifier::create)
                .expectSubscription()
                .expectError(AccessDeniedException.class)
                .verify();
    }

    private void testSuccessfulCreateLeaseRequest() {
        Mockito.doReturn(Mono.just("leaseRequestId"))
                .when(mockLeaseRequestService).createLeaseRequest(any());

        R2dbcLeaseRequest leaseRequest = new R2dbcLeaseRequest();
        leaseRequest.setUserId("user1");
        leaseRequest.setLibraryId("library1");

        securedLeaseRequestService
                .createLeaseRequest(leaseRequest)
                .as(StepVerifier::create)
                .expectSubscription()
                .expectNextCount(1L)
                .expectComplete()
                .verify();
    }


    @Test
    @WithMockUser(authorities = "library1:" + ROLE_LIBRARIAN)
    void shouldAllowAcceptLeaseWhenUserIsLibrarian() {
        mockAcceptLeaseRequest();
        mockLeaseRequestDao();

        DefaultAcceptedLease lease = new DefaultAcceptedLease();
        lease.setLeaseRequestId("leaseRequest1");

        securedLeaseRequestService
                .acceptLeaseRequest(lease)
                .as(StepVerifier::create)
                .expectSubscription()
                .expectComplete()
                .verify();

    }

    private void mockAcceptLeaseRequest() {
        Mockito.doReturn(Mono.empty())
                .when(mockLeaseRequestService)
                .acceptLeaseRequest(any());
    }

    private void mockLeaseRequestDao() {
        R2dbcLeaseRequest leaseRequest = new R2dbcLeaseRequest();
        leaseRequest.setLibraryId("library1");

        Mockito.doReturn(Mono.just(leaseRequest))
                .when(mockLeaseRequestDao).getLeaseRequest(any());
    }


    @Test
    @WithMockUser(authorities = "library1:" + ROLE_STUDENT)
    void shouldDenyAcceptLeaseWhenUserIsNotLibrarian() {
        mockAcceptLeaseRequest();
        mockLeaseRequestDao();

        DefaultAcceptedLease lease = new DefaultAcceptedLease();
        lease.setLeaseRequestId("leaseRequest1");

        securedLeaseRequestService
                .acceptLeaseRequest(lease)
                .as(StepVerifier::create)
                .expectSubscription()
                .expectError(AccessDeniedException.class)
                .verify();
    }

    @Test
    @WithMockUser(authorities = "library1:" + ROLE_LIBRARIAN)
    void shouldAllowRejectLeaseWhenUserIsLibrarian() {
        mockRejectLeaseRequest();
        mockLeaseRequestDao();

        R2dbcRejectedLease rejectedLease = new R2dbcRejectedLease();
        rejectedLease.setLeaseRequestId(UUID.randomUUID());

        securedLeaseRequestService
                .rejectLeaseRequest(rejectedLease)
                .as(StepVerifier::create)
                .expectSubscription()
                .expectComplete()
                .verify();

    }

    private void mockRejectLeaseRequest() {
        Mockito.doReturn(Mono.empty())
                .when(mockLeaseRequestService).rejectLeaseRequest(any());
    }

    @Test
    @WithMockUser(authorities = "library1:" + ROLE_STUDENT)
    void shouldDenyRejectLeaseWhenUserIsNotLibrarian() {
        mockRejectLeaseRequest();
        mockLeaseRequestDao();

        R2dbcRejectedLease rejectedLease = new R2dbcRejectedLease();
        rejectedLease.setLeaseRequestId(UUID.randomUUID());

        securedLeaseRequestService
                .rejectLeaseRequest(rejectedLease)
                .as(StepVerifier::create)
                .expectSubscription()
                .expectError(AccessDeniedException.class)
                .verify();
    }
}