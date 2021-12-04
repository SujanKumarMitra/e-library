package com.github.sujankumarmitra.libraryservice.v1.controller.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.sujankumarmitra.libraryservice.v1.model.Package;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;

/**
 * @author skmitra
 * @since Dec 04/12/21, 2021
 */
@Getter
@Setter
public class JacksonCreatePackageRequest extends Package {

    @JsonIgnore
    private String id;
    @NotEmpty
    private String name;
    @NotEmpty
    private Set<JacksonCreatePackageItemRequest> items;
    private Set<JacksonCreatePackageTagRequest> tags;

}
