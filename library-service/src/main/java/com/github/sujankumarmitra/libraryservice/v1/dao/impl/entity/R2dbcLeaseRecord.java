package com.github.sujankumarmitra.libraryservice.v1.dao.impl.entity;

import com.github.sujankumarmitra.libraryservice.v1.model.LeaseRecord;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.relational.core.mapping.Column;

import java.util.UUID;

import static com.github.sujankumarmitra.libraryservice.v1.util.StringUtil.nullableToString;

/**
 * @author skmitra
 * @since Dec 07/12/21, 2021
 */
@Getter
@Setter
public final class R2dbcLeaseRecord extends LeaseRecord {
    @Column("lease_request_id")
    private UUID leaseRequestId;
    @Column("start_time")
    private Long startTime;
    @Column("end_time")
    private Long endTime;
    @Column("relinquished")
    private Boolean relinquished;

    @Override
    public String getLeaseRequestId() {
        return nullableToString(leaseRequestId);
    }

    public UUID getLeaseRequestUuid() {
        return leaseRequestId;
    }

    @Override
    public Boolean isRelinquished() {
        return relinquished;
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
