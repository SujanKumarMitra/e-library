package com.github.sujankumarmitra.libraryservice.v1.dao.impl.entity;

import com.github.sujankumarmitra.libraryservice.v1.model.LeaseRequest;
import com.github.sujankumarmitra.libraryservice.v1.model.LeaseStatus;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Objects;
import java.util.UUID;

import static com.github.sujankumarmitra.libraryservice.v1.util.StringUtil.nullableToString;

/**
 * @author skmitra
 * @since Dec 06/12/21, 2021
 */
@Getter
@Setter
@Table("lease_requests")
public final class R2dbcLeaseRequest extends LeaseRequest {
    @Id
    private UUID id;
    @Column("book_id")
    private UUID bookId;
    @Column("user_id")
    private String userId;
    private LeaseStatus status;
    private Long timestamp;

    @Transient
    public UUID getUuid() {
        return id;
    }

    public String getId() {
        return nullableToString(id);
    }

    public String getBookId() {
        return nullableToString(bookId);
    }

    @Transient
    public UUID getBookUuid() {
        return bookId;
    }


    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
