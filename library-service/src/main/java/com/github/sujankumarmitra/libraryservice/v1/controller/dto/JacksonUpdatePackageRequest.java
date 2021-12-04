package com.github.sujankumarmitra.libraryservice.v1.controller.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Size;
import java.util.Set;

/**
 * @author skmitra
 * @since Dec 04/12/21, 2021
 */
@Getter
@Setter
public class JacksonUpdatePackageRequest {

    @JsonIgnore
    private String id;
    @Size(min = 1)
    private String name;
    @Size(min = 1)
    private Set<JacksonUpdatePackageItemRequest> items;
    private Set<JacksonUpdatePackageTagRequest> tags;

}
