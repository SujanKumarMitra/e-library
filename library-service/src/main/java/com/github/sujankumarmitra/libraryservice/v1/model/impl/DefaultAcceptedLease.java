package com.github.sujankumarmitra.libraryservice.v1.model.impl;

import com.github.sujankumarmitra.libraryservice.v1.model.AcceptedLease;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author skmitra
 * @since Dec 08/12/21, 2021
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DefaultAcceptedLease extends AcceptedLease {
    private String leaseRequestId;
    private Long startTimeInEpochMilliseconds;
    private Long durationInMilliseconds;
    private Boolean relinquished;

    @Override
    public Boolean isRelinquished() {
        return relinquished;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
}
