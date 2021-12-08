package com.github.sujankumarmitra.libraryservice.v1.controller.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.github.sujankumarmitra.libraryservice.v1.model.LeaseStatus;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;

/**
 * @author skmitra
 * @since Dec 07/12/21, 2021
 */
@Getter
@Setter
@JsonTypeInfo(use = NAME, property = "status")
@JsonSubTypes({
        @Type(value = JacksonValidAcceptLeaseRequestRequest.class, name = "ACCEPTED"),
        @Type(value = JacksonValidRejectLeaseRequestRequest.class, name = "REJECTED")
})
@Data
public abstract class JacksonValidHandleLeaseRequestRequest {
    @JsonIgnore
    private String leaseRequestId;

    public abstract LeaseStatus getStatus();
}
