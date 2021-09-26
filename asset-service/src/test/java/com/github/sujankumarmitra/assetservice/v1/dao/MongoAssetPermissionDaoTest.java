package com.github.sujankumarmitra.assetservice.v1.dao;

import com.github.javafaker.Faker;
import com.github.sujankumarmitra.assetservice.v1.model.AssetPermission;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Collections.emptySet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

/**
 * @author skmitra
 * @since Sep 25/09/21, 2021
 */
@DataMongoTest
@Slf4j
class MongoAssetPermissionDaoTest {

    public static final String VALID_ASSET_ID = "VALID_ASSET_ID";
    public static final String VALID_SUBJECT_ID = "VALID_SUBJECT_ID";

    @Autowired
    private ReactiveMongoTemplate mongoTemplate;
    private MongoAssetPermissionDao daoUnderTest;


    @BeforeEach
    void setUp() {
        daoUnderTest = new MongoAssetPermissionDao(mongoTemplate);
    }

    @AfterEach
    void tearDown() {
        mongoTemplate.remove(MongoAssetDocument.class).all().block();
    }

    @Test
    void givenValidAssetIdAndNoPermission_whenUpsert_shouldDoInsert() {
        MongoAssetDocument assetDocument = MongoAssetDocument
                .newBuilder()
                .id(VALID_ASSET_ID)
                .name("NAME")
                .permissions(emptySet())
                .build();

        log.info("{}", mongoTemplate.insert(assetDocument).block());
        MongoAssetPermissionDocument permissionDocument = MongoAssetPermissionDocument.newBuilder()
                .assetId(VALID_ASSET_ID)
                .subjectId(VALID_SUBJECT_ID)
                .grantStartEpochSecond(System.currentTimeMillis())
                .grantDurationInMillis(-1)
                .build();


        daoUnderTest.upsert(permissionDocument).block();

        MongoAssetDocument updatedAssetDocument = mongoTemplate.findOne(
                query(where("_id").is(VALID_ASSET_ID)), MongoAssetDocument.class).block();

        assertThat(updatedAssetDocument.getPermissions().size()).isEqualTo(1);

        MongoAssetPermissionDocument insertedPermissionDocument = updatedAssetDocument.getPermissions().iterator().next();

        log.info("{}", insertedPermissionDocument);

        assertThat(insertedPermissionDocument.getAssetId()).isEqualTo(permissionDocument.getAssetId());
        assertThat(insertedPermissionDocument.getSubjectId()).isEqualTo(permissionDocument.getSubjectId());
        assertThat(insertedPermissionDocument.getGrantDurationInMillis()).isEqualTo(permissionDocument.getGrantDurationInMillis());
        assertThat(insertedPermissionDocument.getGrantStartEpochSecond()).isEqualTo(permissionDocument.getGrantStartEpochSecond());

    }

    @Test
    void givenValidAssetIdAndExistingPermission_whenUpsert_shouldDoUpdate() {
        MongoAssetPermissionDocument oldPermission = MongoAssetPermissionDocument.newBuilder()
                .assetId(VALID_ASSET_ID)
                .subjectId(VALID_SUBJECT_ID)
                .grantStartEpochSecond(System.currentTimeMillis() - Duration.ofDays(30).toMillis())
                .grantDurationInMillis(-1)
                .build();

        Faker faker = new Faker();
        Set<MongoAssetPermissionDocument> randomPermissions = Stream
                .generate(faker.name()::username)
                .limit(9)
                .map(username ->
                        MongoAssetPermissionDocument.newBuilder()
                                .assetId(VALID_ASSET_ID)
                                .subjectId(username)
                                .grantStartEpochSecond(faker.number().randomNumber())
                                .grantDurationInMillis(faker.number().randomNumber())
                                .build())
                .collect(Collectors.toSet());

        randomPermissions.add(oldPermission);

        Set<MongoAssetPermissionDocument> oldPermissionsSet = randomPermissions;

        MongoAssetDocument assetDocument = MongoAssetDocument
                .newBuilder()
                .id(VALID_ASSET_ID)
                .name("NAME")
                .permissions(oldPermissionsSet)
                .build();


        System.out.println("Inserted Permission Count:: " + mongoTemplate.save(assetDocument).block().getPermissions().size());

        long currentTimestamp = System.currentTimeMillis();
        long durationInMillis = Duration.ofDays(30).toMillis();

        MongoAssetPermissionDocument newPermission = MongoAssetPermissionDocument.newBuilder()
                .assetId(VALID_ASSET_ID)
                .subjectId(VALID_SUBJECT_ID)
                .grantDurationInMillis(durationInMillis)
                .grantStartEpochSecond(currentTimestamp)
                .build();

        daoUnderTest.upsert(newPermission).block();

        MongoAssetDocument updatedAssetDocument = mongoTemplate.findOne(
                query(where("_id").is(VALID_ASSET_ID)), MongoAssetDocument.class).block();

        System.out.println("Fetched Permission Count:: " + updatedAssetDocument.getPermissions().size());
        assertThat(updatedAssetDocument.getPermissions().size()).isEqualTo(10);

        MongoAssetPermissionDocument newPersistedPermission = updatedAssetDocument
                .getPermissions()
                .stream()
                .filter(perm -> perm.getSubjectId().equals(VALID_SUBJECT_ID))
                .findFirst()
                .get();

        assertThat(newPersistedPermission.getGrantStartEpochSecond()).isEqualTo(currentTimestamp);
        assertThat(newPersistedPermission.getGrantDurationInMillis()).isEqualTo(durationInMillis);


    }

    @Test
    void givenValidAssetIdAndSubjectId_whenFindOne_shouldFetchPermission() {
        MongoAssetPermissionDocument storedPermission = MongoAssetPermissionDocument.newBuilder()
                .assetId(VALID_ASSET_ID)
                .subjectId(VALID_SUBJECT_ID)
                .grantStartEpochSecond(System.currentTimeMillis())
                .grantDurationInMillis(-1)
                .build();

        MongoAssetDocument assetDocument = MongoAssetDocument
                .newBuilder()
                .id(VALID_ASSET_ID)
                .name("NAME")
                .permissions(Set.of(storedPermission))
                .build();


        System.out.println(mongoTemplate.insert(assetDocument).block());

        AssetPermission fetchedPermission = daoUnderTest.findOne(VALID_ASSET_ID, VALID_SUBJECT_ID).block();

        System.out.println(fetchedPermission);

        assertThat(fetchedPermission.getAssetId()).isEqualTo(storedPermission.getAssetId());
        assertThat(fetchedPermission.getSubjectId()).isEqualTo(storedPermission.getSubjectId());
        assertThat(fetchedPermission.getGrantDurationInMillis()).isEqualTo(storedPermission.getGrantDurationInMillis());
        assertThat(fetchedPermission.getGrantStartEpochSecond()).isEqualTo(storedPermission.getGrantStartEpochSecond());

    }

    @Test
    void givenInvalidAssetId_whenFindOne_shouldEmitNothing() {
        Mono<AssetPermission> assetPermission = daoUnderTest.findOne("INVALID_ASSET_ID", "INVALID_SUBJECT_ID");

        StepVerifier.create(assetPermission)
                .expectComplete()
                .verify();
    }


}