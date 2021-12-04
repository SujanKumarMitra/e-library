package com.github.sujankumarmitra.libraryservice.v1.controller.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

/**
 * @author skmitra
 * @since Dec 04/12/21, 2021
 */
@Getter
@Setter
public class JacksonUpdatePackageTagRequest {

    @JsonIgnore
    private String id;
    @JsonIgnore
    private String packageId;
    @NotEmpty
    private String key;
    @NotEmpty
    private String value;
}
