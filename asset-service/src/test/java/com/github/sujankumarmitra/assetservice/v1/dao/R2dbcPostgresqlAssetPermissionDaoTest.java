package com.github.sujankumarmitra.assetservice.v1.dao;

import com.github.javafaker.Faker;
import com.github.sujankumarmitra.assetservice.TestcontainersExtension;
import com.github.sujankumarmitra.assetservice.v1.exception.AssetNotFoundException;
import com.github.sujankumarmitra.assetservice.v1.model.AssetPermission;
import com.github.sujankumarmitra.assetservice.v1.model.DefaultAssetPermission;
import io.r2dbc.spi.Result;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

import static com.github.sujankumarmitra.assetservice.v1.model.AssetPermission.INFINITE_GRANT_DURATION;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author skmitra
 * @since Nov 16/11/21, 2021
 */
@DataR2dbcTest
@ExtendWith(TestcontainersExtension.class)
public class R2dbcPostgresqlAssetPermissionDaoTest {

    @Autowired
    private R2dbcEntityTemplate entityTemplate = null;
    private R2dbcPostgresqlAssetPermissionDao permissionDao;

    private static PostgreSQLContainer<?> container = new PostgreSQLContainer("postgres");

    public static List<? extends GenericContainer<?>> getManagedContainers() {
        return List.of(container);
    }

    @BeforeEach
    void setUp() {
        permissionDao = new R2dbcPostgresqlAssetPermissionDao(entityTemplate.getDatabaseClient());
    }

    @AfterEach
    void tearDown() {
        entityTemplate
                .getDatabaseClient()
                .inConnectionMany(conn ->
                        Flux.from(conn.createBatch()
                                .add("DELETE FROM asset_permissions")
                                .add("DELETE FROM assets")
                                .execute()))
                .flatMap(result -> result.getRowsUpdated())
                .blockLast();

    }

    @DynamicPropertySource
    static void registerR2dbcProps(DynamicPropertyRegistry registry) {

        registry.add("spring.r2dbc.url", () -> container.getJdbcUrl()
                .replace("jdbc", "r2dbc"));
        registry.add("spring.r2dbc.username", container::getUsername);
        registry.add("spring.r2dbc.password", container::getPassword);

    }


    @Test
    void givenValidAssetIdAndNoPermission_whenUpsert_shouldDoInsert() {

        UUID id = UUID.randomUUID();
        String name = new Faker().name().name();

        AssetPermission permission = DefaultAssetPermission
                .newBuilder()
                .assetId(id.toString())
                .subjectId("subjectId")
                .grantDurationInMilliseconds(INFINITE_GRANT_DURATION)
                .grantStartEpochMilliseconds(System.currentTimeMillis())
                .build();

        entityTemplate
                .getDatabaseClient()
                .sql("INSERT INTO assets VALUES ($1,$2)")
                .bind("$1", id)
                .bind("$2", name)
                .fetch()
                .rowsUpdated()
                .flatMap(updateCount -> permissionDao.upsert(permission))
                .then(entityTemplate.getDatabaseClient()
                        .sql("SELECT * FROM asset_permissions")
                        .fetch()
                        .all()
                        .count())
                .as(StepVerifier::create)
                .expectSubscription()
                .expectNext(1L)
                .verifyComplete();
    }

    @Test
    void givenValidAssetIdAndExistingPermission_whenUpsert_shouldDoUpdate() {
        UUID id = UUID.randomUUID();
        String name = new Faker().name().name();

        AssetPermission oldPermission = DefaultAssetPermission
                .newBuilder()
                .assetId(id.toString())
                .subjectId("subjectId")
                .grantDurationInMilliseconds(INFINITE_GRANT_DURATION)
                .grantStartEpochMilliseconds(System.currentTimeMillis())
                .build();

        AssetPermission newExpectedPermission = DefaultAssetPermission
                .newBuilder()
                .assetId(id.toString())
                .subjectId("subjectId")
                .grantDurationInMilliseconds(Duration.ofMinutes(10).toMillis())
                .grantStartEpochMilliseconds(System.currentTimeMillis())
                .build();


        entityTemplate.getDatabaseClient()
                .inConnection(conn -> Mono.from(
                        conn.createStatement("INSERT INTO assets VALUES ($1,$2)")
                                .bind("$1", id)
                                .bind("$2", name)
                                .execute()))
                .flatMapMany(Result::getRowsUpdated)
                .next()
                .then(entityTemplate.getDatabaseClient()
                        .inConnection(conn -> Mono.from(
                                conn.createStatement("INSERT INTO asset_permissions VALUES ($1,$2,$3,$4)")
                                        .bind("$1", id)
                                        .bind("$2", oldPermission.getSubjectId())
                                        .bind("$3", oldPermission.getGrantStartEpochMilliseconds())
                                        .bind("$4", oldPermission.getGrantDurationInMilliseconds())
                                        .execute())))
                .flatMapMany(Result::getRowsUpdated)
                .then(permissionDao.upsert(newExpectedPermission))
                .then(entityTemplate
                        .getDatabaseClient()
                        .sql("SELECT * FROM asset_permissions")
                        .fetch()
                        .all()
                        .next()
                        .map(map -> DefaultAssetPermission
                                .newBuilder()
                                .assetId(map.get("asset_id").toString())
                                .subjectId(map.get("subject_id").toString())
                                .grantDurationInMilliseconds(Long.parseLong(map.get("grant_duration").toString()))
                                .grantStartEpochMilliseconds(Long.parseLong(map.get("grant_start").toString()))
                                .build()))
                .cast(AssetPermission.class)
                .as(StepVerifier::create)
                .expectSubscription()
                .consumeNextWith(newActualPermission -> {
                    System.out.println("Old:: " + oldPermission);
                    System.out.println("New Expected:: " + newExpectedPermission);
                    System.out.println("New Actual:: " + newActualPermission);

                    assertThat(newExpectedPermission).isEqualTo(newActualPermission);
                })
                .verifyComplete();


    }

    @Test
    void givenValidAssetIdAndSubjectId_whenFindOne_shouldFetchPermission() {

        UUID assetId = UUID.randomUUID();
        String name = new Faker().name().name();

        AssetPermission expectedPermission = DefaultAssetPermission
                .newBuilder()
                .assetId(assetId.toString())
                .subjectId("subjectId")
                .grantStartEpochMilliseconds(System.currentTimeMillis())
                .grantDurationInMilliseconds(INFINITE_GRANT_DURATION)
                .build();


        entityTemplate.getDatabaseClient()
                .sql("INSERT INTO assets VALUES ($1,$2)")
                .bind("$1", assetId)
                .bind("$2", name)
                .fetch()
                .rowsUpdated()
                .then(entityTemplate.getDatabaseClient()
                        .sql("INSERT INTO asset_permissions VALUES ($1,$2,$3,$4)")
                        .bind("$1", UUID.fromString(expectedPermission.getAssetId()))
                        .bind("$2", expectedPermission.getSubjectId())
                        .bind("$3", expectedPermission.getGrantStartEpochMilliseconds())
                        .bind("$4", expectedPermission.getGrantDurationInMilliseconds())
                        .fetch()
                        .rowsUpdated())
                .then(permissionDao.findOne(expectedPermission.getAssetId(), expectedPermission.getSubjectId()))
                .as(StepVerifier::create)
                .expectSubscription()
                .consumeNextWith(actualPermission -> {
                    System.out.println("Expected:: " + expectedPermission);
                    System.out.println("Actual:: " + actualPermission);

                    assertThat(expectedPermission).isEqualTo(actualPermission);
                })
                .verifyComplete();


    }

    @Test
    void givenInvalidAssetId_whenFindOne_shouldEmitNothing() {

        UUID id = UUID.randomUUID();
        String subjectId = "subject_id";

        permissionDao.findOne(id + "invalid chars", subjectId)
                .as(StepVerifier::create)
                .expectSubscription()
                .expectNextCount(0L)
                .verifyComplete();

    }

    @Test
    void givenInvalidAssetId_whenUpsert_shouldEmitError() {

        AssetPermission permission = DefaultAssetPermission
                .newBuilder()
                .assetId("invalid_id")
                .subjectId("subjectId")
                .grantStartEpochMilliseconds(System.currentTimeMillis())
                .grantDurationInMilliseconds(INFINITE_GRANT_DURATION)
                .build();

        permissionDao.upsert(permission)
                .as(StepVerifier::create)
                .expectSubscription()
                .expectError(AssetNotFoundException.class)
                .verify();

    }

}