package com.github.sujankumarmitra.libraryservice.v1.service.impl;

import com.github.sujankumarmitra.libraryservice.LazySpringBootTest;
import com.github.sujankumarmitra.libraryservice.v1.dao.PackageDao;
import com.github.sujankumarmitra.libraryservice.v1.dao.impl.entity.R2dbcPackage;
import com.github.sujankumarmitra.libraryservice.v1.service.PackageService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;

/**
 * @author skmitra
 * @since Jan 27/01/22, 2022
 */
@Slf4j
@LazySpringBootTest
class SecuredPackageServiceTest {

    @Autowired
    private PackageService securedPackageService;
    @MockBean
    private DefaultPackageService mockPackageService;
    @MockBean
    private PackageDao mockPackageDao;

    @Test
    void shouldInjectBean() {
        assertNotNull(securedPackageService);
    }

    @Test
    @WithMockUser(authorities = "library1:ROLE_TEACHER")
    void givenTeacherUser_whenCreatePackage_shouldCreatePackage() {
        testCreatePackage();
    }

    @Test
    @WithMockUser(authorities = "library1:ROLE_LIBRARIAN")
    void givenLibrarianUser_whenCreatePackage_shouldCreatePackage() {
        testCreatePackage();
    }

    private void testCreatePackage() {
        R2dbcPackage aPackage = new R2dbcPackage();
        aPackage.setLibraryId("library1");

        Mockito.doReturn(Mono.just("packageId"))
                .when(mockPackageService).createPackage(aPackage);

        securedPackageService
                .createPackage(aPackage)
                .as(StepVerifier::create)
                .expectSubscription()
                .expectNext("packageId")
                .expectComplete()
                .verify();
    }

    @Test
    @WithMockUser(authorities = "library1:ROLE_TEACHER")
    void givenTeacherUser_whenDeletePackage_shouldDeletePackage() {
        testDeletePackage();
    }

    @Test
    @WithMockUser(authorities = "library1:ROLE_LIBRARIAN")
    void givenLibrarianUser_whenDeletePackage_shouldDeletePackage() {
        testDeletePackage();
    }

    @Test
    @WithMockUser(authorities = "library2:ROLE_LIBRARIAN")
    void givenUserWithNoAuthorityOnPackageLibrary_whenDeletePackage_shouldEmitError() {

        R2dbcPackage aPackage = new R2dbcPackage();
        aPackage.setLibraryId("library1");

        Mockito.doReturn(Mono.just(aPackage))
                .when(mockPackageDao).getPackage("packageId");

        securedPackageService
                .deletePackage("packageId")
                .as(StepVerifier::create)
                .expectSubscription()
                .expectErrorSatisfies(err -> log.info("Thrown=", err))
                .verify();
    }

    private void testDeletePackage() {
        String packageId = UUID.randomUUID().toString();

        Mockito.doReturn(Mono.empty())
                .when(mockPackageService).deletePackage(packageId);

        R2dbcPackage aPackage = new R2dbcPackage();
        aPackage.setLibraryId("library1");

        Mockito.doReturn(Mono.just(aPackage))
                .when(mockPackageDao).getPackage(packageId);


        securedPackageService
                .deletePackage(packageId)
                .as(StepVerifier::create)
                .expectSubscription()
                .expectComplete()
                .verify();
    }


    @Test
    @WithMockUser(authorities = "library1:ROLE_STUDENT")
    void givenStudentUser_whenGetPackages_shouldGetPackages() {
        testGetPackages();
    }

    @Test
    @WithMockUser(authorities = "library1:ROLE_TEACHER")
    void givenTeacherUser_whenGetPackages_shouldGetPackages() {
        testGetPackages();
    }

    @Test
    @WithMockUser(authorities = "library1:ROLE_LIBRARIAN")
    void givenLibrarianUser_whenGetPackages_shouldGetPackages() {
        testGetPackages();
    }

    @Test
    @WithMockUser(authorities = "library2:ROLE_TEACHER")
    void givenUserWithNoAuthorityOnGivenLibrary_whenGetPackage_shouldEmitError() {
        securedPackageService
                .getPackages("library1", 1)
                .as(StepVerifier::create)
                .expectSubscription()
                .expectErrorSatisfies(err -> log.info("Thrown=", err))
                .verify();
    }

    @Test
    @WithMockUser(authorities = "library1:ROLE_TEACHER")
    void givenTeacherUser_whenUpdatePackage_shouldUpdate() {
        testUpdate();
    }

    @Test
    @WithMockUser(authorities = "library1:ROLE_LIBRARIAN")
    void givenLibrarianUser_whenUpdatePackage_shouldUpdate() {
        testUpdate();
    }

    @Test
    @WithMockUser(authorities = "library2:ROLE_LIBRARIAN")
    void givenUserWithNoAuthorityOnGivenLibrary_whenUpdatePackage_shouldEmitError() {
        R2dbcPackage aPackage = new R2dbcPackage();
        aPackage.setLibraryId("library1");

        securedPackageService
                .updatePackage(aPackage)
                .as(StepVerifier::create)
                .expectSubscription()
                .expectErrorSatisfies(err -> log.info("Thrown=",err))
                .verify();
    }

    private void testUpdate() {
        Mockito.doReturn(Mono.empty())
                .when(mockPackageService).updatePackage(any());

        R2dbcPackage aPackage = new R2dbcPackage();
        aPackage.setLibraryId("library1");

        securedPackageService
                .updatePackage(aPackage)
                .as(StepVerifier::create)
                .expectSubscription()
                .expectComplete()
                .verify();
    }

    private void testGetPackages() {
        Mockito.doReturn(Flux.empty())
                .when(mockPackageService)
                .getPackages(eq("library1"), anyInt());

        securedPackageService
                .getPackages("library1", 1)
                .as(StepVerifier::create)
                .expectSubscription()
                .expectComplete()
                .verify();

        Mockito.doReturn(Flux.empty())
                .when(mockPackageService)
                .getPackagesByName(eq("library1"), anyString(), anyInt());

        securedPackageService
                .getPackagesByName("library1", "prefix", 1)
                .as(StepVerifier::create)
                .expectSubscription()
                .expectComplete()
                .verify();
    }
}