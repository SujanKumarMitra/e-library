package com.github.sujankumarmitra.libraryservice.v1.service.impl;

import com.github.sujankumarmitra.libraryservice.LazySpringBootTest;
import com.github.sujankumarmitra.libraryservice.v1.dao.LeaseRequestDao;
import com.github.sujankumarmitra.libraryservice.v1.service.ActiveLeaseService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static com.github.sujankumarmitra.libraryservice.v1.security.SecurityConstants.ROLE_LIBRARIAN;
import static com.github.sujankumarmitra.libraryservice.v1.security.SecurityConstants.ROLE_STUDENT;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;

/**
 * @author skmitra
 * @since Jan 29/01/22, 2022
 */
@Slf4j
@LazySpringBootTest
class SecuredActiveLeaseServiceTest {
    @Autowired
    private ActiveLeaseService securedActiveLeaseService;
    @MockBean
    private DefaultActiveLeaseService mockActiveLeaseService;
    @MockBean
    private LeaseRequestDao leaseRequestDao;

    @Test
    void shouldInjectBean() {
        Assertions.assertNotNull(securedActiveLeaseService);
    }

    @Test
    @WithMockUser(authorities = "library1:" + ROLE_LIBRARIAN)
    void shouldAllowGetAllActiveLeases_whenUserIsLibrarian() {
        Mockito.doReturn(Flux.empty())
                .when(mockActiveLeaseService).getAllActiveLeases(any(), anyInt());

        securedActiveLeaseService
                .getAllActiveLeases("library1", 0)
                .as(StepVerifier::create)
                .expectSubscription()
                .expectComplete()
                .verify();
    }

    @Test
    @WithMockUser(authorities = "library1:" + ROLE_STUDENT)
    void shouldDenyGetAllActiveLeases_whenUserIsNotLibrarian() {
        Mockito.doReturn(Flux.empty())
                .when(mockActiveLeaseService).getAllActiveLeases(any(), anyInt());

        securedActiveLeaseService
                .getAllActiveLeases("library1", 0)
                .as(StepVerifier::create)
                .expectSubscription()
                .expectError(AccessDeniedException.class)
                .verify();
    }

}