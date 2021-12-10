package com.github.sujankumarmitra.libraryservice.v1.util;

import com.github.sujankumarmitra.libraryservice.v1.dao.impl.entity.R2dbcLeaseRequest;
import org.springframework.r2dbc.core.ConnectionAccessor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static com.github.sujankumarmitra.libraryservice.v1.model.LeaseStatus.PENDING;

/**
 * @author skmitra
 * @since Dec 06/12/21, 2021
 */
public class LeaseRequestDaoTestUtils {

    public static Mono<R2dbcLeaseRequest> insertLeaseRequest(ConnectionAccessor connAccessor, UUID bookId) {
        R2dbcLeaseRequest leaseRequest = new R2dbcLeaseRequest();

        leaseRequest.setBookId(bookId);
        leaseRequest.setStatus(PENDING);
        leaseRequest.setTimestamp(System.currentTimeMillis());
        leaseRequest.setUserId("user_id");

        return connAccessor.inConnectionMany(connection ->
                        Flux.from(connection
                                .createStatement("INSERT INTO lease_requests(book_id,user_id,status,timestamp) VALUES ($1,$2,$3,$4) RETURNING id")
                                .bind("$1", leaseRequest.getBookUuid())
                                .bind("$2", leaseRequest.getUserId())
                                .bind("$3", leaseRequest.getStatus().toString())
                                .bind("$4", leaseRequest.getTimestamp())
                                .execute()))
                .flatMap(result -> result.map((row, rowMetadata) -> row.get("id", UUID.class)))
                .next()
                .doOnNext(leaseRequest::setId)
                .thenReturn(leaseRequest);
    }

}
