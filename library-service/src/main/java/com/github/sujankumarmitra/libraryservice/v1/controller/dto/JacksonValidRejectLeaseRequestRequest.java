package com.github.sujankumarmitra.libraryservice.v1.controller.dto;

import com.github.sujankumarmitra.libraryservice.v1.model.LeaseStatus;
import lombok.Data;

import static com.github.sujankumarmitra.libraryservice.v1.model.LeaseStatus.REJECTED;

/**
 * @author skmitra
 * @since Dec 07/12/21, 2021
 */
@Data
public class JacksonValidRejectLeaseRequestRequest extends JacksonValidHandleLeaseRequestRequest {
    private String reasonPhrase;

    @Override
    public LeaseStatus getStatus() {
        return REJECTED;
    }
}
