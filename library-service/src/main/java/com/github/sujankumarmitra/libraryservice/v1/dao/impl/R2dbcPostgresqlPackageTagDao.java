package com.github.sujankumarmitra.libraryservice.v1.dao.impl;

import com.github.sujankumarmitra.libraryservice.v1.dao.PackageTagDao;
import com.github.sujankumarmitra.libraryservice.v1.dao.impl.entity.R2dbcPackageTag;
import com.github.sujankumarmitra.libraryservice.v1.exception.DefaultErrorDetails;
import com.github.sujankumarmitra.libraryservice.v1.exception.DuplicateTagKeyException;
import com.github.sujankumarmitra.libraryservice.v1.exception.PackageNotFoundException;
import com.github.sujankumarmitra.libraryservice.v1.model.PackageTag;
import io.r2dbc.spi.R2dbcDataIntegrityViolationException;
import io.r2dbc.spi.Result;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.Statement;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * @author skmitra
 * @since Nov 23/11/21, 2021
 */
@Repository
@AllArgsConstructor
@Slf4j
public class R2dbcPostgresqlPackageTagDao implements PackageTagDao {

    public static final String INSERT_STATEMENT = "INSERT INTO package_tags(package_id,key,value) VALUES ($1,$2,$3) RETURNING id";
    public static final String SELECT_STATEMENT = "SELECT id, package_id, key, value FROM package_tags WHERE package_id=$1";
    public static final String UPDATE_STATEMENT = "UPDATE package_tags SET value=$1 WHERE id=$2";
    public static final String DELETE_STATEMENT = "DELETE FROM package_tags WHERE package_id=$1";
    public static final String UNIQUE_TAG_KEY_CONSTRAINT_NAME = "unq_package_tags_book_id_key";

    @NonNull
    private final DatabaseClient databaseClient;

    @Override
    @Transactional
    public Flux<String> createTags(Collection<? extends PackageTag> tags) {
        return Flux.defer(() -> {
            if (tags == null) {
                log.debug("given tags is null");
                return Flux.error(new NullPointerException("given tags is null"));
            }

            if(tags.isEmpty()) {
                log.debug("Empty collection tags. returning empty Flux");
                return Flux.empty();
            }

            return databaseClient.inConnectionMany(connection -> {
                        Statement statement = connection.createStatement(INSERT_STATEMENT);

                        for (PackageTag tag : tags) {
                            String packageId = tag.getPackageId();
                            UUID uuid;
                            try {
                                uuid = UUID.fromString(packageId);
                            } catch (IllegalArgumentException ex) {
                                log.debug("{} is not a valid uuid, returning Flux.error(PackageNotFoundException)", packageId);
                                return Flux.error(new PackageNotFoundException(packageId));
                            }
                            statement = statement
                                    .bind("$1", uuid)
                                    .bind("$2", tag.getKey())
                                    .bind("$3", tag.getValue())
                                    .add();
                        }

                        return Flux.from(statement.execute());
                    })
                    .flatMapSequential(result -> result.map((row, rowMetadata) -> row.get("id", UUID.class)))
                    .onErrorMap(R2dbcDataIntegrityViolationException.class, err -> {
                        log.debug("DB integrity error {}", err.getMessage());
                        String message = err.getMessage();

                        if (message.contains(UNIQUE_TAG_KEY_CONSTRAINT_NAME))
                            return new DuplicateTagKeyException("tag with given key already exists for given bookId");
                        else
                            return new PackageNotFoundException(
                                    List.of(new DefaultErrorDetails("some bookId(s) is/are invalid")));
                    })
                    .map(Object::toString);
        });
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<PackageTag> getTagsByPackageId(String packageId) {
        return Flux.defer(() -> {
            if (packageId == null) {
                log.debug("given packageId is null");
                return Flux.error(new NullPointerException("given packageId is null"));
            }
            UUID uuid;
            try {
                uuid = UUID.fromString(packageId);
            } catch (IllegalArgumentException e) {
                log.debug("{} is not valid uuid, return Flux.empty()", packageId);
                return Flux.empty();
            }
            return databaseClient
                    .sql(SELECT_STATEMENT)
                    .bind("$1", uuid)
                    .map(this::mapToR2dbcPackageTag)
                    .all()
                    .cast(PackageTag.class);
        });

    }

    @Override
    @Transactional
    public Mono<Void> updateTags(Collection<? extends PackageTag> tags) {
        return Mono.defer(() -> {
            if (tags == null) {
                log.debug("given tags is null");
                return Mono.error(new NullPointerException("given tags is null"));
            }
            return databaseClient.inConnectionMany(connection -> {
                        Statement statement = connection.createStatement(UPDATE_STATEMENT);
                        for (PackageTag tag : tags) {
                            String id = tag.getId();
                            UUID uuid;
                            try {
                                uuid = UUID.fromString(id);
                            } catch (Exception e) {
                                log.debug("{} is not valid uuid, skipping update", id);
                                continue;
                            }
                            statement = statement
                                    .bind("$1", tag.getValue())
                                    .bind("$2", uuid)
                                    .add();
                        }

                        return Flux.from(statement.execute());
                    }).flatMap(Result::getRowsUpdated)
                    .reduce(Integer::sum)
                    .doOnSuccess(updateCount -> log.debug("author update count {}", updateCount))
                    .then();
        });
    }

    @Override
    @Transactional
    public Mono<Void> deleteTagsByPackageId(String packageId) {
        return Mono.defer(() -> {
            if (packageId == null) {
                log.debug("null packageId, return Mono.error(NullPointerException)");
                return Mono.error(new NullPointerException("packageId must be non-null"));
            }
            UUID uuid;
            try {
                uuid = UUID.fromString(packageId);
            } catch (IllegalArgumentException e) {
                log.debug("{} is invalid uuid, returning Mono.empty()", packageId);
                return Mono.empty();
            }

            return this.databaseClient
                    .sql(DELETE_STATEMENT)
                    .bind("$1", uuid)
                    .fetch()
                    .rowsUpdated()
                    .doOnSuccess(deleteCount -> log.debug("tags delete count: {}", deleteCount))
                    .then();
        });
    }

    private R2dbcPackageTag mapToR2dbcPackageTag(Row row) {
        R2dbcPackageTag tag = new R2dbcPackageTag();

        tag.setId(row.get("id", UUID.class));
        tag.setPackageId(row.get("package_id", UUID.class));
        tag.setKey(row.get("key", String.class));
        tag.setValue(row.get("value", String.class));

        return tag;
    }
}
