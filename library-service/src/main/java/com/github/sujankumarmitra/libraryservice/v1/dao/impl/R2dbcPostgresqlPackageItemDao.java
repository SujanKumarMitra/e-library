package com.github.sujankumarmitra.libraryservice.v1.dao.impl;

import com.github.sujankumarmitra.libraryservice.v1.dao.PackageItemDao;
import com.github.sujankumarmitra.libraryservice.v1.dao.impl.entity.R2dbcPackageItem;
import com.github.sujankumarmitra.libraryservice.v1.exception.BookNotFoundException;
import com.github.sujankumarmitra.libraryservice.v1.exception.DefaultErrorDetails;
import com.github.sujankumarmitra.libraryservice.v1.exception.DuplicatePackageItemException;
import com.github.sujankumarmitra.libraryservice.v1.exception.PackageNotFoundException;
import com.github.sujankumarmitra.libraryservice.v1.model.PackageItem;
import io.r2dbc.spi.*;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * @author skmitra
 * @since Nov 27/11/21, 2021
 */
@Repository
@AllArgsConstructor
@Slf4j
public class R2dbcPostgresqlPackageItemDao implements PackageItemDao {

    public static final String INSERT_STATEMENT = "INSERT INTO package_items(package_id,book_id) VALUES ($1,$2) RETURNING id";
    public static final String SELECT_STATEMENT = "SELECT id,package_id,book_id FROM package_items WHERE package_id=$1";
    public static final String UPDATE_STATEMENT = "UPDATE package_items SET package_id=$1, book_id=$2 WHERE id=$3";
    public static final String DELETE_BY_PACKAGE_ID_STATEMENT = "DELETE FROM package_items WHERE package_id=$1";
    public static final String DELETE_BY_ID_STATEMENT = "DELETE FROM package_items WHERE id=$1";
    public static final String PACKAGES_FOREIGN_KEY_CONSTRAINT_NAME = "fk_package_items_packages";
    public static final String UNIQUE_PACKAGE_ITEM_ID_CONSTRAINT_NAME = "unq_package_items_package_id_book_id";
    public static final String BOOKS_FOREIGN_KEY_CONSTRAINT_NAME = "fk_package_items_books";
    @NonNull
    private final DatabaseClient databaseClient;

    @Override
    public Flux<String> createItems(Collection<? extends PackageItem> packageItems) {
        return Flux.defer(() -> {
            if (packageItems == null) {
                log.debug("given packageItems is null");
                return Flux.error(new NullPointerException("packageItems can't be null"));
            }

            if (packageItems.isEmpty()) {
                log.debug("empty collection, returning empty flux");
                return Flux.empty();
            }

            return this.databaseClient.inConnectionMany(connection ->
                            prepareAndExecuteStatement(packageItems, connection))
                    .flatMapSequential(result -> result.map(((row, rowMetadata) -> row.get("id", UUID.class))))
                    .doOnNext(insertedId -> log.debug("inserted package item id:: {}", insertedId))
                    .map(Object::toString)
                    .onErrorMap(R2dbcDataIntegrityViolationException.class, this::translateException);
        });
    }

    @NonNull
    private Flux<? extends Result> prepareAndExecuteStatement(Collection<? extends PackageItem> packageItems, Connection connection) {
        Statement statement = connection.createStatement(INSERT_STATEMENT);
        for (PackageItem item : packageItems) {
            UUID packageUuid;
            String stringPackageId = item.getPackageId();

            try {
                packageUuid = UUID.fromString(stringPackageId);
            } catch (IllegalArgumentException ex) {
                log.debug("{} is not valid uuid for package, returning Flux.error(PackageNotFoundException)", stringPackageId);
                return Flux.error(new PackageNotFoundException(stringPackageId));
            }

            UUID bookUuid;
            String stringBookId = item.getBookId();

            try {
                bookUuid = UUID.fromString(stringBookId);
            } catch (IllegalArgumentException ex) {
                log.debug("{} is not valid uuid for book, returning Flux.error(BookNotFoundException)", stringBookId);
                return Flux.error(new BookNotFoundException(stringBookId));
            }

            statement = statement
                    .bind("$1", packageUuid)
                    .bind("$2", bookUuid)
                    .add();

        }

        return Flux.from(statement.execute());
    }

    private Throwable translateException(R2dbcDataIntegrityViolationException err) {
        String msg = err.getMessage();
        if (msg.contains(PACKAGES_FOREIGN_KEY_CONSTRAINT_NAME))
            return new PackageNotFoundException(
                    List.of(new DefaultErrorDetails("some package(s) does not exist")));
        if (msg.contains(BOOKS_FOREIGN_KEY_CONSTRAINT_NAME))
            return new BookNotFoundException(
                    List.of(new DefaultErrorDetails("some book(s) does not exist")));
        if(msg.contains(UNIQUE_PACKAGE_ITEM_ID_CONSTRAINT_NAME))
            return new DuplicatePackageItemException(
                    List.of(new DefaultErrorDetails("item with bookId(s) already exists")));
        return err;
    }

    @Override
    public Flux<PackageItem> getItemsByPackageId(String packageId) {
        UUID packageUuid;
        try {
            packageUuid = UUID.fromString(packageId);
        } catch (IllegalArgumentException e) {
            log.debug("{} is not valid uuid, returning empty Flux", packageId);
            return Flux.empty();
        }
        return this.databaseClient
                .sql(SELECT_STATEMENT)
                .bind("$1", packageUuid)
                .map(this::mapToR2dbcPackageItem)
                .all()
                .cast(PackageItem.class);
    }

    private R2dbcPackageItem mapToR2dbcPackageItem(Row row) {
        R2dbcPackageItem item = new R2dbcPackageItem();

        item.setId(row.get("id", UUID.class));
        item.setPackageId(row.get("package_id", UUID.class));
        item.setBookId(row.get("book_id", UUID.class));

        return item;
    }

    @Override
    public Mono<Void> deleteItemsByPackageId(String packageId) {
        return Mono.defer(() -> {
            UUID uuid;
            try {
                uuid = UUID.fromString(packageId);
            } catch (IllegalArgumentException e) {
                log.debug("{} is not valid packageId, return empty Mono", packageId);
                return Mono.empty();
            }
            return this.databaseClient
                    .sql(DELETE_BY_PACKAGE_ID_STATEMENT)
                    .bind("$1", uuid)
                    .fetch()
                    .rowsUpdated()
                    .doOnSuccess(deleteCount -> log.debug("deleted package count {}", deleteCount))
                    .then();
        });
    }

}
