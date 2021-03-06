package com.github.sujankumarmitra.libraryservice.v1.dao.impl;

import com.github.sujankumarmitra.libraryservice.v1.dao.PackageDao;
import com.github.sujankumarmitra.libraryservice.v1.dao.PackageItemDao;
import com.github.sujankumarmitra.libraryservice.v1.dao.PackageTagDao;
import com.github.sujankumarmitra.libraryservice.v1.dao.impl.entity.R2dbcPackage;
import com.github.sujankumarmitra.libraryservice.v1.dao.impl.entity.R2dbcPackageItem;
import com.github.sujankumarmitra.libraryservice.v1.dao.impl.entity.R2dbcPackageTag;
import com.github.sujankumarmitra.libraryservice.v1.model.Package;
import com.github.sujankumarmitra.libraryservice.v1.model.PackageItem;
import com.github.sujankumarmitra.libraryservice.v1.model.PackageTag;
import io.r2dbc.spi.Row;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple3;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author skmitra
 * @since Nov 26/11/21, 2021
 */
@Repository
@Slf4j
@AllArgsConstructor
public class R2dbcPostgresqlPackageDao implements PackageDao {

    public static final String INSERT_STATEMENT = "INSERT INTO packages(library_id, name) values($1,$2) RETURNING id";
    public static final String SELECT_BY_ID_STATEMENT = "SELECT id,library_id,name FROM packages WHERE id=$1";
    public static final String SELECT_BY_NAME_STATEMENT = "SELECT id,library_id,name FROM packages WHERE library_id=$4 AND name LIKE $1 LIMIT $3 OFFSET $2";
    public static final String SELECT_ALL_STATEMENT = "SELECT id,library_id,name FROM packages WHERE library_id=$3 LIMIT $2 OFFSET $1";
    public static final String UPDATE_STATEMENT = "UPDATE packages SET library_id=$1, name=$2 WHERE id=$3";
    public static final String DELETE_STATEMENT = "DELETE FROM packages WHERE id=$1";

    @NonNull
    private final DatabaseClient databaseClient;
    @NonNull
    private final PackageItemDao packageItemDao;
    @NonNull
    private final PackageTagDao packageTagDao;

    @Override
    @Transactional
    public Mono<String> createPackage(@NonNull Package aPackage) {
        R2dbcPackage r2dbcPackage = new R2dbcPackage(aPackage);
        return this.databaseClient
                .sql(INSERT_STATEMENT)
                .bind("$1", r2dbcPackage.getLibraryId())
                .bind("$2", r2dbcPackage.getName())
                .map(row -> row.get("id", UUID.class))
                .one()
                .doOnSuccess(id -> log.debug("created package {}", id))
                .doOnSuccess(id -> setPackageIds(id, r2dbcPackage))
                .flatMap(id ->
                        packageItemDao
                                .createItems(r2dbcPackage.getItems())
                                .then()
                                .thenReturn(id))
                .flatMap(id ->
                        packageTagDao
                                .createTags(r2dbcPackage.getTags())
                                .then()
                                .thenReturn(id))
                .map(Object::toString);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<Package> getPackages(String libraryId, int skip, int limit) {
        return databaseClient
                .sql(SELECT_ALL_STATEMENT)
                .bind("$1", skip)
                .bind("$2", limit)
                .bind("$3", libraryId)
                .map(this::mapToR2dbcPackage)
                .all()
                .flatMapSequential(aPackage -> packageItemDao
                        .getItemsByPackageId(aPackage.getId())
                        .cast(R2dbcPackageItem.class)
                        .collect(Collectors.toCollection(HashSet::new))
                        .doOnNext(aPackage::setItems)
                        .thenReturn(aPackage)
                        .map(Package::getId)
                        .flatMapMany(packageTagDao::getTagsByPackageId)
                        .cast(R2dbcPackageTag.class)
                        .collect(Collectors.toCollection(HashSet::new))
                        .doOnNext(aPackage::setTags)
                        .thenReturn(aPackage))
                .cast(Package.class);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<Package> getPackagesByNameStartingWith(String libraryId, @NonNull String prefix, int skip, int limit) {
        return databaseClient
                .sql(SELECT_BY_NAME_STATEMENT)
                .bind("$1", prefix + "%")
                .bind("$2", skip)
                .bind("$3", limit)
                .bind("$4", libraryId)
                .map(this::mapToR2dbcPackage)
                .all()
                .flatMapSequential(aPackage -> packageItemDao
                        .getItemsByPackageId(aPackage.getId())
                        .cast(R2dbcPackageItem.class)
                        .collect(Collectors.toCollection(HashSet::new))
                        .doOnNext(aPackage::setItems)
                        .thenReturn(aPackage)
                        .map(Package::getId)
                        .flatMapMany(packageTagDao::getTagsByPackageId)
                        .cast(R2dbcPackageTag.class)
                        .collect(Collectors.toCollection(HashSet::new))
                        .doOnNext(aPackage::setTags)
                        .thenReturn(aPackage))
                .cast(Package.class);
    }

    private void setPackageIds(UUID packageId, R2dbcPackage aPackage) {
        aPackage.setId(packageId);
        aPackage.getItems()
                .forEach(packageItem -> packageItem.setPackageId(packageId));
        aPackage.getTags()
                .forEach(tag -> tag.setPackageId(packageId));
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Package> getPackage(@NonNull String packageId) {
        return Mono.defer(() -> {
            UUID id;
            try {
                id = UUID.fromString(packageId);
            } catch (Exception e) {
                log.debug("{} not valid uuid, returning Mono.empty()", packageId);
                return Mono.empty();
            }
            Mono<R2dbcPackage> packageMono = databaseClient
                    .sql(SELECT_BY_ID_STATEMENT)
                    .bind("$1", id)
                    .map(this::mapToR2dbcPackage)
                    .one();

            Mono<Set<PackageItem>> packageItemsMono = packageItemDao
                    .getItemsByPackageId(packageId)
                    .collect(Collectors.toCollection(HashSet::new));

            Mono<Set<PackageTag>> packageTagsMono = packageTagDao
                    .getTagsByPackageId(packageId)
                    .collect(Collectors.toCollection(HashSet::new));

            return Mono.zip(packageMono, packageItemsMono, packageTagsMono)
                    .map(this::assemblePackage);
        });

    }

    private Package assemblePackage(Tuple3<R2dbcPackage, Set<PackageItem>, Set<PackageTag>> tuple) {
        R2dbcPackage aPackage = tuple.getT1();
        Set<PackageItem> packageItems = tuple.getT2();
        Set<PackageTag> packageTags = tuple.getT3();

        aPackage.addAllItems(packageItems);
        aPackage.addAllTags(packageTags);

        return aPackage;
    }

    private R2dbcPackage mapToR2dbcPackage(Row row) {
        R2dbcPackage r2dbcPackage = new R2dbcPackage();

        r2dbcPackage.setId(row.get("id", UUID.class));
        r2dbcPackage.setLibraryId(row.get("library_id", String.class));
        r2dbcPackage.setName(row.get("name", String.class));

        return r2dbcPackage;
    }

    @Override
    @Transactional
    public Mono<Void> updatePackage(@NonNull Package aPackage) {
        return Mono.defer(() -> {
            String id = aPackage.getId();

            if (id == null) {
                log.debug("Package.getId() returned null, returning Mono.error(NullPointerException)");
                return Mono.error(new NullPointerException("packageId can't be null"));
            }

            UUID uuid;
            try {
                uuid = UUID.fromString(id);
            } catch (IllegalArgumentException ex) {
                log.debug("{} is not valid uuid, returning empty Mono", id);
                return Mono.empty();
            }


            return select(uuid)
                    .doOnNext(fetchedPackage -> applyUpdates(aPackage, fetchedPackage))
                    .flatMap(fetchedPackage -> this.databaseClient
                            .sql(UPDATE_STATEMENT)
                            .bind("$1", fetchedPackage.getLibraryId())
                            .bind("$2", fetchedPackage.getName())
                            .bind("$3", fetchedPackage.getUuid())
                            .fetch()
                            .rowsUpdated()
                            .then())
                    .then(Mono.defer(() -> {
                        if (aPackage.getItems() == null) {
                            log.debug("Package.getItems() is null, no changes made to items of packageId, {}", uuid);
                            return Mono.empty();
                        }
                        return packageItemDao
                                .deleteItemsByPackageId(id)
                                .thenMany(packageItemDao.createItems(aPackage.getItems()))
                                .then();
                    }))
                    .flatMap(fetchedPackage -> {
                        if (aPackage.getTags() == null) {
                            log.debug("Package.getTags() is null, no changes made to tags of packageId, {}", uuid);
                            return Mono.empty();
                        }
                        return packageTagDao
                                .deleteTagsByPackageId(id)
                                .thenMany(packageTagDao.createTags(aPackage.getTags()))
                                .then();
                    });

        });
    }

    private void applyUpdates(Package aPackage, R2dbcPackage fetchedPackage) {
        if(aPackage.getLibraryId() != null) {
            fetchedPackage.setLibraryId(aPackage.getLibraryId());
        }

        if (aPackage.getName() != null) {
            fetchedPackage.setName(aPackage.getName());
        }
    }


    Mono<R2dbcPackage> select(UUID id) {
        return this.databaseClient
                .sql(SELECT_BY_ID_STATEMENT)
                .bind("$1", id)
                .map(this::mapToR2dbcPackage)
                .one();
    }

    @Override
    @Transactional
    public Mono<Void> deletePackage(@NonNull String packageId) {
        return Mono.defer(() -> {
            UUID uuid;
            try {
                uuid = UUID.fromString(packageId);
            } catch (Exception e) {
                log.debug("{} not a valid uuid, return Mono.empty()", packageId);
                return Mono.empty();
            }
            return this.databaseClient
                    .sql(DELETE_STATEMENT)
                    .bind("$1", uuid)
                    .fetch()
                    .rowsUpdated()
                    .doOnSuccess(deleteCount -> {
                        if (deleteCount > 0) log.debug("deleted package with id {}", packageId);
                        else log.debug("package not found with id {}", packageId);
                    })
                    .then();
        });
    }
}
