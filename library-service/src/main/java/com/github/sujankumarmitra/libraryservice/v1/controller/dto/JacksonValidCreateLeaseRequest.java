package com.github.sujankumarmitra.libraryservice.v1.controller.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.sujankumarmitra.libraryservice.v1.model.LeaseRequest;
import com.github.sujankumarmitra.libraryservice.v1.model.LeaseStatus;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;

/**
 * @author skmitra
 * @since Dec 06/12/21, 2021
 */
@Data
public class JacksonValidCreateLeaseRequest extends LeaseRequest {

    @JsonIgnore
    private String id;
    @NotEmpty
    private String bookId;
    @JsonIgnore
    private String userId;
    @JsonIgnore
    private LeaseStatus status;
    @JsonIgnore
    private Long timestamp;

}
