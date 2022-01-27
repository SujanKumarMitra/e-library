package com.github.sujankumarmitra.libraryservice.v1.dao.impl;

import com.github.sujankumarmitra.libraryservice.v1.dao.PackageItemDao;
import com.github.sujankumarmitra.libraryservice.v1.dao.PackageTagDao;
import com.github.sujankumarmitra.libraryservice.v1.dao.impl.entity.R2dbcPackage;
import com.github.sujankumarmitra.libraryservice.v1.model.Package;
import com.github.sujankumarmitra.libraryservice.v1.model.PackageItem;
import com.github.sujankumarmitra.libraryservice.v1.model.PackageTag;
import com.github.sujankumarmitra.libraryservice.v1.util.PackageDaoTestUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.r2dbc.connection.init.ScriptUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;

import static com.github.sujankumarmitra.libraryservice.v1.util.DaoTestUtils.truncateAllTables;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.r2dbc.connection.init.ScriptUtils.executeSqlScript;

/**
 * @author skmitra
 * @since Nov 26/11/21, 2021
 */
@Slf4j
class R2dbcPostgresqlPackageDaoTest extends AbstractDataR2dbcPostgreSQLContainerDependentTest {

    private R2dbcPostgresqlPackageDao packageDao;
    @Mock
    private PackageItemDao mockPackageItemDao;
    @Mock
    private PackageTagDao mockPackageTagDao;
    @Autowired
    private R2dbcEntityTemplate entityTemplate = null;

    @BeforeEach
    void setUp() {
        packageDao = new R2dbcPostgresqlPackageDao(
                entityTemplate.getDatabaseClient(),
                mockPackageItemDao,
                mockPackageTagDao);
    }

    @AfterEach
    void tearDown() {
        truncateAllTables(entityTemplate.getDatabaseClient())
                .block();
    }

    @Test
    void givenValidPackage_whenCreate_shouldCreate() {
        R2dbcPackage r2dbcPackage = new R2dbcPackage();

        r2dbcPackage.setLibraryId("library_id");
        r2dbcPackage.setName("package_name");

        Mockito.doReturn(Flux.empty())
                .when(mockPackageItemDao).createItems(any());
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
                .getItemsByPackageId(any());
        Mockito.doReturn(Flux.empty())
                .when(mockPackageTagDao)
                .getTagsByPackageId(any());

        PackageDaoTestUtils
                .insertDummyPackage(this.entityTemplate.getDatabaseClient())
                .doOnSuccess(insertedPackage -> expectedPackage.setId(insertedPackage.getUuid()))
                .doOnSuccess(insertedPackage -> expectedPackage.setLibraryId(insertedPackage.getLibraryId()))
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
                .getItemsByPackageId(any());

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
                .getItemsByPackageId(any());
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
                .when(mockPackageItemDao).deleteItemsByPackageId(any());

        Mockito.doReturn(Mono.empty())
                .when(mockPackageTagDao).deleteTagsByPackageId(any());

        Mockito.doReturn(Flux.empty())
                .when(mockPackageItemDao).createItems(any());

        Mockito.doReturn(Flux.empty())
                .when(mockPackageTagDao).createTags(any());

        R2dbcPackage expectedPackage = new R2dbcPackage();

        PackageDaoTestUtils
                .insertDummyPackage(entityTemplate.getDatabaseClient())
                .doOnSuccess(insertedPackage -> expectedPackage.setId(insertedPackage.getUuid()))
                .doOnSuccess(insertedPackage -> expectedPackage.setLibraryId(insertedPackage.getLibraryId()))
                .doOnSuccess(insertedPackage -> expectedPackage.setName(insertedPackage.getName()))
                .doOnSuccess(that -> expectedPackage.setName("updated_name"))
                .thenReturn(expectedPackage)
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
                    log.info("expected package:: {}", expectedPackage);
                    log.info("actual package:: {}", actualPackage);

                    assertThat(actualPackage).isEqualTo(expectedPackage);
                })
                .verifyComplete();


    }

    @Test
    void givenNonExistingPackage_whenUpdate_shouldEmitEmpty() {

        Mockito.doReturn(Mono.empty())
                .when(mockPackageItemDao).deleteItemsByPackageId(any());

        Mockito.doReturn(Mono.empty())
                .when(mockPackageTagDao).deleteTagsByPackageId(any());

        Mockito.doReturn(Flux.empty())
                .when(mockPackageItemDao).createItems(any());

        Mockito.doReturn(Flux.empty())
                .when(mockPackageTagDao).createTags(any());


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
                .when(mockPackageItemDao).updateItems(any());
        Mockito.doReturn(Mono.empty())
                .when(mockPackageTagDao).updateTags(any());

        Package aPackage = new Package() {
            @Override
            public String getId() {
                return "malformed";
            }

            @Override
            public String getLibraryId() {
                return "library_id";
            }

            @Override
            public String getName() {
                return "name";
            }

            @Override
            @SuppressWarnings("unchecked")
            public Set<PackageItem> getItems() {
                return Collections.emptySet();
            }

            @Override
            @SuppressWarnings("unchecked")
            public Set<PackageTag> getTags() {
                return Collections.emptySet();
            }
        };

        Mono.just(aPackage)
                .flatMap(packageDao::updatePackage)
                .as(StepVerifier::create)
                .expectSubscription()
                .verifyComplete();

    }


    @Test
    void givenNonExistingPackage_whenDelete_shouldEmitComplete() {

        packageDao.deletePackage(UUID.randomUUID().toString())
                .as(StepVerifier::create)
                .expectSubscription()
                .expectNextCount(0L)
                .verifyComplete();
    }

    @Test
    void givenMalformedPackageId_whenDelete_shouldEmitComplete() {
        packageDao.deletePackage("malformed_uuid")
                .as(StepVerifier::create)
                .expectSubscription()
                .expectNextCount(0L)
                .verifyComplete();
    }

    @Test
    void givenValidPackageId_whenDelete_shouldDeletePackageAndItsDependentEntities() {

        UUID validPackageId = UUID.fromString("d913745e-9328-49ca-94fa-2ca7118ae1d2");

        entityTemplate
                .getDatabaseClient()
                .inConnection(conn -> executeSqlScript(conn, new ClassPathResource("sample_data.sql")))
                .then(packageDao.deletePackage(validPackageId.toString()))
                .then(entityTemplate
                        .getDatabaseClient()
                        .sql("SELECT COUNT(*) FROM packages WHERE id=$1")
                        .bind("$1", validPackageId)
                        .map(row -> row.get(0, Integer.class))
                        .one())
                .as(StepVerifier::create)
                .expectSubscription()
                .expectNext(0)
                .expectComplete()
                .verify();

        entityTemplate
                .getDatabaseClient()
                .sql("SELECT COUNT(*) FROM package_items WHERE package_id=$1")
                .bind("$1", validPackageId)
                .map(row -> row.get(0, Integer.class))
                .one()
                .as(StepVerifier::create)
                .expectSubscription()
                .expectNext(0)
                .expectComplete()
                .verify();


        entityTemplate
                .getDatabaseClient()
                .sql("SELECT COUNT(*) FROM package_tags WHERE package_id=$1")
                .bind("$1", validPackageId)
                .map(row -> row.get(0, Integer.class))
                .one()
                .as(StepVerifier::create)
                .expectSubscription()
                .expectNext(0)
                .expectComplete()
                .verify();
    }

    @Test
    void givenSetOfPackages_whenGetPackages_shouldGetPackages() {

        Mockito.doReturn(Flux.empty())
                        .when(mockPackageItemDao).getItemsByPackageId(any());
        Mockito.doReturn(Flux.empty())
                .when(mockPackageTagDao).getTagsByPackageId(any());

        entityTemplate
                .getDatabaseClient()
                .inConnection(conn -> executeSqlScript(conn, new ClassPathResource("sample_data.sql")))
                .thenMany(packageDao.getPackages("library1", 0, 10))
                .as(StepVerifier::create)
                .expectSubscription()
                .expectNextCount(2L)
                .expectComplete()
                .verify();


    }

    @Test
    void givenSetOfPackages_whenGetPackagesByName_shouldGetPackages() {
        Mockito.doReturn(Flux.empty())
                .when(mockPackageItemDao).getItemsByPackageId(any());
        Mockito.doReturn(Flux.empty())
                .when(mockPackageTagDao).getTagsByPackageId(any());

        entityTemplate
                .getDatabaseClient()
                .inConnection(conn -> executeSqlScript(conn, new ClassPathResource("sample_data.sql")))
                .thenMany(packageDao.getPackagesByNameStartingWith("library1", "I", 0, 10))
                .as(StepVerifier::create)
                .expectSubscription()
                .expectNextCount(1L)
                .expectComplete()
                .verify();
    }

}