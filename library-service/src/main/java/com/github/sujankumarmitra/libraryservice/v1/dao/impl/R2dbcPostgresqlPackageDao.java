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
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple3;

import java.util.List;
import java.util.UUID;

/**
 * @author skmitra
 * @since Nov 26/11/21, 2021
 */
@Repository
@Slf4j
@AllArgsConstructor
public class R2dbcPostgresqlPackageDao implements PackageDao {

    public static final String INSERT_STATEMENT = "INSERT INTO packages(name) values($1) RETURNING id";
    public static final String SELECT_STATEMENT = "SELECT id,name FROM packages WHERE id=$1";
    public static final String UPDATE_STATEMENT = "UPDATE packages SET name=$1 WHERE id=$2";
    public static final String DELETE_STATEMENT = "DELETE FROM packages WHERE id=$1";

    @NonNull
    private final DatabaseClient databaseClient;
    @NonNull
    private final PackageItemDao packageItemDao;
    @NonNull
    private final PackageTagDao packageTagDao;

    @Override
    @Transactional
    public Mono<String> createPackage(Package _package) {
        return Mono.defer(() -> {
            if (_package == null) {
                log.debug("given parameter is null");
                return Mono.error(new NullPointerException("package can't be null"));
            }
            R2dbcPackage r2dbcPackage = new R2dbcPackage(_package);
            return this.databaseClient
                    .sql(INSERT_STATEMENT)
                    .bind("$1", r2dbcPackage.getName())
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
        });
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
    public Mono<Package> getPackage(String packageId) {
        return Mono.defer(() -> {
            if (packageId == null) {
                log.debug("given parameter packageId is null");
                return Mono.error(new NullPointerException("packageId can't be null"));
            }
            UUID id;
            try {
                id = UUID.fromString(packageId);
            } catch (Exception e) {
                log.debug("{} not valid uuid, returning Mono.empty()", packageId);
                return Mono.empty();
            }
            Mono<R2dbcPackage> _packageMono = databaseClient
                    .sql(SELECT_STATEMENT)
                    .bind("$1", id)
                    .map(this::mapToR2dbcPackage)
                    .one();

            Mono<List<PackageItem>> packageItemsMono = packageItemDao
                    .getItemsByPackageId(packageId)
                    .collectList();

            Mono<List<PackageTag>> packageTagsMono = packageTagDao
                    .getTagsByPackageId(packageId)
                    .collectList();

            return Mono.zip(_packageMono, packageItemsMono, packageTagsMono)
                    .map(this::assemblePackage);
        });

    }

    private Package assemblePackage(Tuple3<R2dbcPackage, List<PackageItem>, List<PackageTag>> tuple) {
        R2dbcPackage _package = tuple.getT1();
        List<PackageItem> packageItems = tuple.getT2();
        List<PackageTag> packageTags = tuple.getT3();

        for (PackageItem item : packageItems) {
            _package.getItems().add(new R2dbcPackageItem(item));
        }

        for (PackageTag tag : packageTags) {
            _package.getTags().add(new R2dbcPackageTag(tag));
        }
        return _package;
    }

    private R2dbcPackage mapToR2dbcPackage(Row row) {
        R2dbcPackage _package = new R2dbcPackage();

        _package.setId(row.get("id", UUID.class));
        _package.setName(row.get("name", String.class));

        return _package;
    }

    @Override
    @Transactional
    public Mono<Void> updatePackage(Package _package) {
        return Mono.defer(() -> {
            if (_package == null) {
                log.debug("given package is null");
                return Mono.error(new NullPointerException("package can't be null"));
            }

            UUID uuid;
            try {
                uuid = UUID.fromString(_package.getId());
            } catch (IllegalArgumentException ex) {
                log.debug("{} is not valid uuid, returning empty Mono");
                return Mono.empty();
            }

            R2dbcPackage r2dbcPackage = new R2dbcPackage(_package);

            return this.databaseClient
                    .sql(UPDATE_STATEMENT)
                    .bind("$1", r2dbcPackage.getName())
                    .bind("$2", uuid)
                    .fetch()
                    .rowsUpdated()
                    .then(Mono.justOrEmpty(_package.getItems()))
                    .flatMap(packageItemDao::updateItems)
                    .then(Mono.justOrEmpty(_package.getTags()))
                    .flatMap(packageTagDao::updateTags);
        });
    }

    @Override
    @Transactional
    public Mono<Void> deletePackage(String packageId) {
        return Mono.defer(() -> {
            if (packageId == null) {
                log.debug("given packageId in param is null");
                return Mono.error(new NullPointerException("packageId can't be null"));
            }
            UUID uuid;
            try {
                uuid = UUID.fromString(packageId);
            } catch (Exception e) {
                log.debug("{} not a valid uuid, return Mono.empty()", packageId);
                return Mono.empty();
            }
            return packageItemDao
                    .deleteItemsByPackageId(packageId)
                    .thenReturn(packageId)
                    .flatMap(packageTagDao::deleteTagsByPackageId)
                    .then(this.databaseClient
                            .sql(DELETE_STATEMENT)
                            .bind("$1", uuid)
                            .fetch()
                            .rowsUpdated()
                            .doOnSuccess(deleteCount -> {
                                if (deleteCount > 0)
                                    log.debug("deleted package with id {}", packageId);
                                else
                                    log.debug("package not found with id {}", packageId);

                            })
                            .then());
        });
    }
}
