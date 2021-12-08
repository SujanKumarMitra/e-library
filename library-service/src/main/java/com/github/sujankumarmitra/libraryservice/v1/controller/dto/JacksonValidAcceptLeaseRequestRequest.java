package com.github.sujankumarmitra.libraryservice.v1.controller.dto;

import com.github.sujankumarmitra.libraryservice.v1.model.LeaseStatus;
import lombok.Data;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;

import static com.github.sujankumarmitra.libraryservice.v1.model.AcceptedLease.INFINITE_LEASE_DURATION;
import static com.github.sujankumarmitra.libraryservice.v1.model.LeaseStatus.ACCEPTED;

/**
 * @author skmitra
 * @since Dec 07/12/21, 2021
 */
@Data
public class JacksonValidAcceptLeaseRequestRequest extends JacksonValidHandleLeaseRequestRequest {
    @NotNull
    private Long startTimeInEpochMilliseconds;
    @NotNull
    private Long durationInMilliseconds;

    @Override
    public LeaseStatus getStatus() {
        return ACCEPTED;
    }

    @AssertTrue(message = "start time must be present or future")
    public boolean isValidStartTime() {
        return startTimeInEpochMilliseconds >= System.currentTimeMillis();
    }

    @AssertTrue(message = "duration must be either -1 or positive")
    public boolean isValidDuration() {
        return durationInMilliseconds > 0 ||
                durationInMilliseconds.equals(INFINITE_LEASE_DURATION);
    }
}
