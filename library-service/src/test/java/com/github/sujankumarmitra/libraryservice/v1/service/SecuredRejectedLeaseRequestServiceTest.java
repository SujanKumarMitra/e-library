package com.github.sujankumarmitra.libraryservice.v1.service;

import com.github.sujankumarmitra.libraryservice.LazySpringBootTest;
import com.github.sujankumarmitra.libraryservice.v1.dao.LeaseRequestDao;
import com.github.sujankumarmitra.libraryservice.v1.dao.impl.entity.R2dbcLeaseRequest;
import com.github.sujankumarmitra.libraryservice.v1.service.impl.DefaultRejectedLeaseRequestService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

/**
 * @author skmitra
 * @since Jan 29/01/22, 2022
 */
@Slf4j
@LazySpringBootTest
class SecuredRejectedLeaseRequestServiceTest {

    @Autowired
    private RejectedLeaseRequestService securedRejectedLeaseRequestService;
    @MockBean
    private DefaultRejectedLeaseRequestService mockRejectedLeaseRequestService;
    @MockBean
    private LeaseRequestDao mockLeaseRequestDao;


    @Test
    void shouldInjectBeans() {
        assertNotNull(securedRejectedLeaseRequestService);
    }


    @Test
    @WithMockUser(username = "user1", authorities = "library1:ROLE_STUDENT")
    void shouldAllowGetRejectedLeaseWhenInvokedByOwnedStudentUserAndHasAccessToLibrary() {
        testSuccessfulGetRejectedLeaseRequest();
    }

    @Test
    @WithMockUser(username = "user1", authorities = "library1:ROLE_TEACHER")
    void shouldAllowGetRejectedLeaseWhenInvokedByOwnedTeacherUserAndHasAccessToLibrary() {
        testSuccessfulGetRejectedLeaseRequest();
    }

    @Test
    @WithMockUser(username = "user2", authorities = "library1:ROLE_STUDENT")
    void shouldDenyGetRejectedLeaseWhenOwnerIsDifferent() {
        testFailureGetRejectedLeaseRequest();
    }

    @Test
    @WithMockUser(username = "user1")
    void shouldDenyGetRejectedLeaseWhenOwnerHasNoAuthorityOverLibrary() {
        testFailureGetRejectedLeaseRequest();
    }

    private void testFailureGetRejectedLeaseRequest() {
        mockLeaseRequestDao();
        mockDelegateLeaseRequestService();

        securedRejectedLeaseRequestService
                .getByLeaseRequestId("lease1")
                .as(StepVerifier::create)
                .expectSubscription()
                .expectError(AccessDeniedException.class)
                .verify();
    }

    private void testSuccessfulGetRejectedLeaseRequest() {
        mockLeaseRequestDao();
        mockDelegateLeaseRequestService();

        securedRejectedLeaseRequestService
                .getByLeaseRequestId("lease1")
                .as(StepVerifier::create)
                .expectSubscription()
                .expectComplete()
                .verify();
    }

    private void mockLeaseRequestDao() {
        R2dbcLeaseRequest leaseRequest = new R2dbcLeaseRequest();

        leaseRequest.setUserId("user1");
        leaseRequest.setLibraryId("library1");

        Mockito.doReturn(Mono.just(leaseRequest))
                .when(mockLeaseRequestDao).getLeaseRequest(any());
    }

    private void mockDelegateLeaseRequestService() {
        Mockito.doReturn(Mono.empty())
                .when(mockRejectedLeaseRequestService).getByLeaseRequestId(any());
    }
}