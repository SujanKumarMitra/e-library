package com.github.sujankumarmitra.libraryservice.v1.dao.impl;

import com.github.javafaker.Faker;
import com.github.sujankumarmitra.libraryservice.v1.dao.impl.entity.R2dbcBook;
import lombok.NonNull;
import org.springframework.r2dbc.core.ConnectionAccessor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * @author skmitra
 * @since Nov 24/11/21, 2021
 */
public class BookDaoTestUtils {


    private static Faker faker = new Faker();

    private BookDaoTestUtils() {
    }

    public static Mono<R2dbcBook> insertDummyBook(@NonNull ConnectionAccessor connAccessor) {
        R2dbcBook book = new R2dbcBook();
        book.setTitle(faker.book().title());
        book.setPublisher(faker.book().publisher());
        book.setEdition(faker.idNumber().valid());
        book.setCoverPageImageAssetId(faker.idNumber().valid());

        return connAccessor.inConnectionMany(conn ->
                        Flux.from(conn.createStatement(R2dbcPostgresqlBookDao.INSERT_STATEMENT)
                                .bind("$1", book.getTitle())
                                .bind("$2", book.getPublisher())
                                .bind("$3", book.getEdition())
                                .bind("$4", book.getCoverPageImageAssetId())
                                .execute()))
                .flatMap(result -> result.map((row, __) -> row.get("id", UUID.class)))
                .next()
                .doOnNext(book::setId)
                .thenReturn(book);

    }
}
