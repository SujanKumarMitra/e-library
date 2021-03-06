package com.github.sujankumarmitra.libraryservice.v1.controller.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.sujankumarmitra.libraryservice.v1.model.Package;
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
public class JacksonValidUpdatePackageRequest extends Package {

    @JsonIgnore
    private String id;
    @NotEmpty
    private String libraryId;
    @Size(min = 1)
    private String name;
    @Size(min = 1)
    private Set<JacksonValidUpdatePackageItemRequest> items;
    private Set<JacksonValidUpdatePackageTagRequest> tags;

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
