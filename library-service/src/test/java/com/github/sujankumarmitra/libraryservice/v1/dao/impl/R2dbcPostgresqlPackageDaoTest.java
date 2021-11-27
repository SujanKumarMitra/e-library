package com.github.sujankumarmitra.libraryservice.v1.dao.impl;

import com.github.sujankumarmitra.libraryservice.v1.dao.PackageItemDao;
import com.github.sujankumarmitra.libraryservice.v1.dao.PackageTagDao;
import com.github.sujankumarmitra.libraryservice.v1.dao.impl.entity.R2dbcPackage;
import com.github.sujankumarmitra.libraryservice.v1.dao.impl.entity.R2dbcPackageItem;
import com.github.sujankumarmitra.libraryservice.v1.model.Package;
import com.github.sujankumarmitra.libraryservice.v1.model.PackageItem;
import com.github.sujankumarmitra.libraryservice.v1.model.PackageTag;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

/**
 * @author skmitra
 * @since Nov 26/11/21, 2021
 */
@DataR2dbcTest
@Slf4j
@Testcontainers
class R2dbcPostgresqlPackageDaoTest {

    private R2dbcPostgresqlPackageDao packageDao;
    @Mock
    private PackageItemDao mockPackageItemDao;
    @Mock
    private PackageTagDao mockPackageTagDao;
    @Autowired
    private R2dbcEntityTemplate entityTemplate = null;

    @Container
    private static final PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres");

    @DynamicPropertySource
    static void registerDynamicProperties(DynamicPropertyRegistry registry) {

        registry.add("spring.r2dbc.url", () ->
                postgreSQLContainer.getJdbcUrl().replace("jdbc", "r2dbc"));
        registry.add("spring.r2dbc.username", postgreSQLContainer::getUsername);
        registry.add("spring.r2dbc.password", postgreSQLContainer::getPassword);

    }

    @BeforeEach
    void setUp() {
        packageDao = new R2dbcPostgresqlPackageDao(
                entityTemplate.getDatabaseClient(),
                mockPackageItemDao,
                mockPackageTagDao);
    }

    @AfterEach
    void tearDown() {
        entityTemplate
                .delete(R2dbcPackageItem.class)
                .from("package_items")
                .all()
                .block();

        entityTemplate
                .delete(R2dbcPackage.class)
                .from("packages")
                .all()
                .block();
    }

    @Test
    void givenValidPackage_whenCreate_shouldCreate() {
        R2dbcPackage r2dbcPackage = new R2dbcPackage();
        r2dbcPackage.setName("package_name");

        Mockito.doReturn(Flux.empty())
                .when(mockPackageItemDao).createPackageItems(any());
        Mockito.doReturn(Flux.empty())
                .when(mockPackageTagDao).createTags(any());

        packageDao.createPackage(r2dbcPackage)
                .as(StepVerifier::create)
                .expectSubscription()
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void givenValidPackageId_whenGet_shouldFetchPackage() {

        R2dbcPackage expectedPackage = new R2dbcPackage();

        Mockito.doReturn(Flux.empty())
                .when(mockPackageItemDao)
                .getPackageItemsByPackageId(any());
        Mockito.doReturn(Flux.empty())
                .when(mockPackageTagDao)
                .getTagsByPackageId(any());

        PackageDaoTestUtils
                .insertDummyPackage(this.entityTemplate.getDatabaseClient())
                .doOnSuccess(insertedPackage -> expectedPackage.setId(insertedPackage.getUuid()))
                .doOnSuccess(insertedPackage -> expectedPackage.setName(insertedPackage.getName()))
                .map(Package::getId)
                .flatMap(packageDao::getPackage)
                .as(StepVerifier::create)
                .expectSubscription()
                .consumeNextWith(actualPackage -> {
                    log.info("expected package:: {}", expectedPackage);
                    log.info("actual package:: {}", actualPackage);

                    assertThat(actualPackage).isEqualTo(expectedPackage);
                })
                .verifyComplete();
    }

    @Test
    void givenNonExistingPackage_whenGet_shouldEmitComplete() {
        Mockito.doReturn(Flux.empty())
                .when(mockPackageItemDao)
                .getPackageItemsByPackageId(any());

        Mockito.doReturn(Flux.empty())
                .when(mockPackageTagDao)
                .getTagsByPackageId(any());

        packageDao.getPackage(UUID.randomUUID().toString())
                .as(StepVerifier::create)
                .expectSubscription()
                .expectNextCount(0L)
                .verifyComplete();
    }

    @Test
    void givenMalformedPackageId_whenGet_shouldEmitComplete() {
        Mockito.doReturn(Flux.empty())
                .when(mockPackageItemDao)
                .getPackageItemsByPackageId(any());
        Mockito.doReturn(Flux.empty())
                .when(mockPackageTagDao)
                .getTagsByPackageId(any());

        packageDao.getPackage("malformed_uuid")
                .as(StepVerifier::create)
                .expectSubscription()
                .expectNextCount(0L)
                .verifyComplete();
    }

    @Test
    void givenValidPackage_whenUpdate_shouldUpdate() {

        Mockito.doReturn(Mono.empty())
                .when(mockPackageItemDao).updatePackageItems(any());

        Mockito.doReturn(Mono.empty())
                .when(mockPackageTagDao).updateTags(any());

        R2dbcPackage _package = new R2dbcPackage();

        PackageDaoTestUtils
                .insertDummyPackage(entityTemplate.getDatabaseClient())
                .doOnSuccess(insertedPackage -> _package.setId(insertedPackage.getUuid()))
                .doOnSuccess(insertedPackage -> _package.setName(insertedPackage.getName()))
                .doOnSuccess(__ -> _package.setName("updated_name"))
                .thenReturn(_package)
                .flatMap(packageDao::updatePackage)
                .then(entityTemplate
                        .select(R2dbcPackage.class)
                        .from("packages")
                        .one()
                        .doOnSuccess(aPackage -> aPackage.getItems().clear())
                        .doOnSuccess(aPackage -> aPackage.getTags().clear()))
                .as(StepVerifier::create)
                .expectSubscription()
                .consumeNextWith(actualPackage -> {
                    log.info("expected package:: {}", _package);
                    log.info("actual package:: {}", actualPackage);

                    assertThat(actualPackage).isEqualTo(_package);
                })
                .verifyComplete();


    }

    @Test
    void givenNonExistingPackage_whenUpdate_shouldEmitEmpty() {

        Mockito.doReturn(Mono.empty())
                .when(mockPackageItemDao).updatePackageItems(any());

        Mockito.doReturn(Mono.empty())
                .when(mockPackageTagDao).updateTags(any());

        R2dbcPackage _package = new R2dbcPackage();
        _package.setId(UUID.randomUUID());
        _package.setName("name");

        Mono.just(_package)
                .flatMap(packageDao::updatePackage)
                .as(StepVerifier::create)
                .expectSubscription()
                .verifyComplete();

    }

    @Test
    void givenPackageWithMalformedId_whenUpdate_shouldEmitEmpty() {

        Mockito.doReturn(Mono.empty())
                .when(mockPackageItemDao).updatePackageItems(any());
        Mockito.doReturn(Mono.empty())
                .when(mockPackageTagDao).updateTags(any());

        Package _package = new Package() {
            @Override
            public String getId() {
                return "malformed";
            }

            @Override
            public String getName() {
                return "name";
            }

            @Override
            public Set<? extends PackageItem> getItems() {
                return Collections.emptySet();
            }

            @Override
            public Set<? extends PackageTag> getTags() {
                return Collections.emptySet();
            }
        };

        Mono.just(_package)
                .flatMap(packageDao::updatePackage)
                .as(StepVerifier::create)
                .expectSubscription()
                .verifyComplete();

    }


    @Test
    void givenNonExistingPackage_whenDelete_shouldEmitComplete() {
        Mockito.doReturn(Mono.empty())
                .when(mockPackageItemDao)
                .deletePackageItemsByPackageId(any());

        Mockito.doReturn(Mono.empty())
                .when(mockPackageTagDao)
                .deleteTagsByPackageId(any());

        packageDao.deletePackage(UUID.randomUUID().toString())
                .as(StepVerifier::create)
                .expectSubscription()
                .expectNextCount(0L)
                .verifyComplete();
    }

    @Test
    void givenMalformedPackageId_whenDelete_shouldEmitComplete() {
        Mockito.doReturn(Mono.empty())
                .when(mockPackageItemDao)
                .deletePackageItemsByPackageId(any());

        Mockito.doReturn(Mono.empty())
                .when(mockPackageTagDao)
                .deleteTagsByPackageId(any());

        packageDao.deletePackage("malformed_uuid")
                .as(StepVerifier::create)
                .expectSubscription()
                .expectNextCount(0L)
                .verifyComplete();
    }

}