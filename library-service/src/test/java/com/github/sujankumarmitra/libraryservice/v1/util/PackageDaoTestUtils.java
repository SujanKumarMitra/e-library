package com.github.sujankumarmitra.libraryservice.v1.util;

import com.github.javafaker.Faker;
import com.github.sujankumarmitra.libraryservice.v1.dao.impl.R2dbcPostgresqlPackageDao;
import com.github.sujankumarmitra.libraryservice.v1.dao.impl.entity.R2dbcPackage;
import lombok.NonNull;
import org.springframework.r2dbc.core.ConnectionAccessor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * @author skmitra
 * @since Nov 24/11/21, 2021
 */
public class PackageDaoTestUtils {


    private static final Faker faker = new Faker();

    private PackageDaoTestUtils() {
    }

    public static Mono<R2dbcPackage> insertDummyPackage(@NonNull ConnectionAccessor connAccessor) {
        R2dbcPackage aPackage = new R2dbcPackage();
        aPackage.setLibraryId(faker.idNumber().valid());
        aPackage.setName(faker.book().title());

        return connAccessor.inConnectionMany(conn ->
                        Flux.from(conn.createStatement(R2dbcPostgresqlPackageDao.INSERT_STATEMENT)
                                .bind("$1", aPackage.getLibraryId())
                                .bind("$2", aPackage.getName())
                                .execute()))
                .flatMap(result -> result.map((row, __) -> row.get("id", UUID.class)))
                .next()
                .doOnNext(aPackage::setId)
                .thenReturn(aPackage);

    }
}
