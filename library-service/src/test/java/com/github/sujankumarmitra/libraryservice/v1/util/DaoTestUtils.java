package com.github.sujankumarmitra.libraryservice.v1.util;

import io.r2dbc.spi.Batch;
import lombok.NonNull;
import org.springframework.r2dbc.core.ConnectionAccessor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author skmitra
 * @since Dec 10/12/21, 2021
 */
public class DaoTestUtils {

    private DaoTestUtils() {
    }

    public static Mono<Void> truncateAllTables(@NonNull ConnectionAccessor accessor) {
        return accessor.inConnectionMany(connection -> {
            Batch batch = connection.createBatch()
                    .add("TRUNCATE TABLE books CASCADE")
                    .add("TRUNCATE TABLE packages CASCADE")
                    .add("TRUNCATE TABLE librarians CASCADE");
            return Flux.from(batch.execute());
        }).then();
    }

}
