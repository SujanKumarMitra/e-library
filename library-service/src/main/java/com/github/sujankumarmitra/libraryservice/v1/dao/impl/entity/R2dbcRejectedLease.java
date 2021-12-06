package com.github.sujankumarmitra.libraryservice.v1.dao.impl.entity;

import com.github.sujankumarmitra.libraryservice.v1.model.RejectedLease;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

import static com.github.sujankumarmitra.libraryservice.v1.util.StringUtil.nullableToString;
import static com.github.sujankumarmitra.libraryservice.v1.util.UuidUtil.nullableUuid;

/**
 * @author skmitra
 * @since Dec 06/12/21, 2021
 */
@Table("rejected_lease_requests")
@Getter
@Setter
public final class R2dbcRejectedLease extends RejectedLease {
    @Id
    @Column("lease_request_id")
    private UUID leaseRequestId;
    @Column("reason_phrase")
    private String reasonPhrase;

    @Override
    public String getLeaseRequestId() {
        return nullableToString(leaseRequestId);
    }

    public UUID getLeaseRequestUuid() {
        return leaseRequestId;
    }

    public R2dbcRejectedLease() {
    }

    public R2dbcRejectedLease(RejectedLease rejectedLease) {
        this.leaseRequestId = nullableUuid(rejectedLease.getLeaseRequestId());
        this.reasonPhrase = rejectedLease.getReasonPhrase();
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
