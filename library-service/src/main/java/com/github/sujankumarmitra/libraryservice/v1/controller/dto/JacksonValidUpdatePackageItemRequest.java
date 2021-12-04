package com.github.sujankumarmitra.libraryservice.v1.controller.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.sujankumarmitra.libraryservice.v1.model.PackageItem;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

/**
 * @author skmitra
 * @since Dec 04/12/21, 2021
 */
@Getter
@Setter
public class JacksonValidUpdatePackageItemRequest extends PackageItem {
    @JsonIgnore
    private String id;
    @JsonIgnore
    private String packageId;
    @NotEmpty
    private String bookId;
}
