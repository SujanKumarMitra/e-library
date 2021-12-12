package com.github.sujankumarmitra.assetservice.v1.service;

import com.github.sujankumarmitra.assetservice.v1.dao.AssetDao;
import com.github.sujankumarmitra.assetservice.v1.dao.AssetPermissionDao;
import com.github.sujankumarmitra.assetservice.v1.model.AssetPermission;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static com.github.sujankumarmitra.assetservice.v1.model.AssetPermission.INFINITE_GRANT_DURATION;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.lang.System.currentTimeMillis;
import static java.time.Duration.ofDays;

/**
 * @author skmitra
 * @since Sep 25/09/21, 2021
 */
@ExtendWith(MockitoExtension.class)
class DefaultAssetPermissionServiceTest {

    public static final String VALID_SUBJECT_ID = "VALID_SUBJECT_ID";
    public static final String VALID_ASSET_ID = "VALID_ASSET_ID";
    @Mock
    private AssetPermissionDao permissionDao;
    @Mock
    private AssetDao assetDao;
    private DefaultAssetPermissionService serviceUnderTest;

    @BeforeEach
    void setUp() {
        serviceUnderTest = new DefaultAssetPermissionService(assetDao, permissionDao);
    }

    @Test
    void givenInfiniteGrantDuration_whenCheckedForPermission_shouldEmitTrue() {
        Mockito.doReturn(Mono.just(new AssetPermissionImpl(
                        VALID_ASSET_ID,
                        VALID_SUBJECT_ID,
                        currentTimeMillis(),
                        INFINITE_GRANT_DURATION)))
                .when(permissionDao)
                .findOne(VALID_ASSET_ID, VALID_SUBJECT_ID);


        Mono<Boolean> hasPermission = serviceUnderTest.hasPermission(VALID_ASSET_ID, VALID_SUBJECT_ID);

        StepVerifier
                .create(hasPermission)
                .expectNext(TRUE)
                .verifyComplete();
    }

    @Test
    void givenValidFiniteGrant_whenCheckedForPermission_shouldEmitTrue() {
        Mockito.doReturn(Mono.just(new AssetPermissionImpl(
                        VALID_ASSET_ID,
                        VALID_SUBJECT_ID,
                        tenDaysBeforeToday(),
                        elevenDays())))
                .when(permissionDao)
                .findOne(VALID_ASSET_ID, VALID_SUBJECT_ID);


        Mono<Boolean> hasPermission = serviceUnderTest.hasPermission(VALID_ASSET_ID, VALID_SUBJECT_ID);

        StepVerifier
                .create(hasPermission)
                .expectNext(TRUE)
                .verifyComplete();
    }

    @Test
    void givenExpiredGrant_whenCheckedForPermission_shouldEmitFalse() {
        Mockito.doReturn(Mono.just(new AssetPermissionImpl(
                        VALID_ASSET_ID,
                        VALID_SUBJECT_ID,
                        tenDaysBeforeToday(),
                        nineDays())))
                .when(permissionDao)
                .findOne(VALID_ASSET_ID, VALID_SUBJECT_ID);


        Mono<Boolean> hasPermission = serviceUnderTest.hasPermission(VALID_ASSET_ID, VALID_SUBJECT_ID);

        StepVerifier
                .create(hasPermission)
                .expectNext(FALSE)
                .verifyComplete();
    }

    private long nineDays() {
        return ofDays(9).toMillis();
    }

    private long elevenDays() {
        return ofDays(11).toMillis();
    }

    private long tenDaysBeforeToday() {
        return currentTimeMillis() - ofDays(10).toMillis();
    }

    @Data
    @EqualsAndHashCode(callSuper = false)
    @AllArgsConstructor
    static class AssetPermissionImpl extends AssetPermission {
        String assetId;
        String subjectId;
        Long grantStartEpochMilliseconds;
        Long grantDurationInMilliseconds;
    }
}