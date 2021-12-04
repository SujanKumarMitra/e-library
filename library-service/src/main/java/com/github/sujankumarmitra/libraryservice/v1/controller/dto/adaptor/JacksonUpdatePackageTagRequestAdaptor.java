package com.github.sujankumarmitra.libraryservice.v1.controller.dto.adaptor;

import com.github.sujankumarmitra.libraryservice.v1.controller.dto.JacksonUpdatePackageTagRequest;
import com.github.sujankumarmitra.libraryservice.v1.model.PackageTag;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

/**
 * @author skmitra
 * @since Dec 04/12/21, 2021
 */
@Getter
@Setter
@AllArgsConstructor
public class JacksonUpdatePackageTagRequestAdaptor extends PackageTag {
    @NonNull
    private final JacksonUpdatePackageTagRequest request;

    @Override
    public String getId() {
        return request.getId();
    }

    @Override
    public String getKey() {
        return request.getKey();
    }

    @Override
    public String getValue() {
        return request.getValue();
    }

    @Override
    public String getPackageId() {
        return request.getPackageId();
    }

}
