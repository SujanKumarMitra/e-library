package com.github.sujankumarmitra.libraryservice.v1.model.impl;

import com.github.sujankumarmitra.libraryservice.v1.model.LeaseRecord;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;

/**
 * @author skmitra
 * @since Dec 07/12/21, 2021
 */
@Data
@Getter
@AllArgsConstructor
public class DefaultLeaseRecord extends LeaseRecord {
    @NonNull
    private final String leaseRequestId;
    @NonNull
    private final Long startTime;
    @NonNull
    private final Long endTime;
    @NonNull
    private final Boolean relinquished;

    @Override
    public Boolean isRelinquished() {
        return relinquished;
    }
}
