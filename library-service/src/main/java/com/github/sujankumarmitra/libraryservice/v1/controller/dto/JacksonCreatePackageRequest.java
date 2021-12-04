package com.github.sujankumarmitra.libraryservice.v1.controller.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.Set;

/**
 * @author skmitra
 * @since Dec 04/12/21, 2021
 */
@Getter
@Setter
public class JacksonCreatePackageRequest {

    @NotEmpty
    private String name;
    @Size(min = 1)
    private Set<JacksonCreatePackageItemRequest> items;
    private Set<JacksonCreatePackageTagRequest> tags;

}
