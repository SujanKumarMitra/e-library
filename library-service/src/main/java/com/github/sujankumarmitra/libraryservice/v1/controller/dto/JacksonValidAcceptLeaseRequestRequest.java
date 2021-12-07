package com.github.sujankumarmitra.libraryservice.v1.controller.dto;

import com.github.sujankumarmitra.libraryservice.v1.model.LeaseStatus;
import lombok.Data;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;

import static com.github.sujankumarmitra.libraryservice.v1.model.LeaseStatus.ACCEPTED;

/**
 * @author skmitra
 * @since Dec 07/12/21, 2021
 */
@Data
public class JacksonValidAcceptLeaseRequestRequest extends JacksonValidHandleLeaseRequestRequest {
    @NotNull
    private Long startTime;
    @NotNull
    private Long endTime;

    @Override
    public LeaseStatus getStatus() {
        return ACCEPTED;
    }

    @AssertTrue(message = "start time must be present or future")
    public boolean isValidStartTime() {
        return startTime >= System.currentTimeMillis();
    }

    @AssertTrue(message = "end time must be greater than start time")
    public boolean isValidEndTime() {
        return endTime > startTime;
    }
}
