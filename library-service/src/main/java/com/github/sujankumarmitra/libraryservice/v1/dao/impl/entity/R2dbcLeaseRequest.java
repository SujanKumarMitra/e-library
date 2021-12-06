package com.github.sujankumarmitra.libraryservice.v1.dao.impl.entity;

import com.github.sujankumarmitra.libraryservice.v1.model.LeaseRequest;
import com.github.sujankumarmitra.libraryservice.v1.model.LeaseStatus;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

import static com.github.sujankumarmitra.libraryservice.v1.util.StringUtil.nullableToString;

/**
 * @author skmitra
 * @since Dec 06/12/21, 2021
 */
@Getter
@Setter
public final class R2dbcLeaseRequest extends LeaseRequest {
    private UUID id;
    private UUID bookId;
    private String userId;
    private LeaseStatus status;
    private Long timestamp;

    public UUID getUuid() {
        return id;
    }

    public String getId() {
        return nullableToString(id);
    }

    public String getBookId() {
        return nullableToString(bookId);
    }

    public UUID getBookUuid() {
        return bookId;
    }
}
