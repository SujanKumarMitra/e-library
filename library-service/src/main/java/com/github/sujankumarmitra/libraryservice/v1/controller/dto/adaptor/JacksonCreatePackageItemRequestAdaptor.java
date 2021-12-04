package com.github.sujankumarmitra.libraryservice.v1.controller.dto.adaptor;

import com.github.sujankumarmitra.libraryservice.v1.controller.dto.JacksonCreatePackageItemRequest;
import com.github.sujankumarmitra.libraryservice.v1.model.PackageItem;
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
public class JacksonCreatePackageItemRequestAdaptor extends PackageItem {
    @NonNull
    private final JacksonCreatePackageItemRequest request;

    @Override
    public String getId() {
        return request.getId();
    }

    @Override
    public String getPackageId() {
        return request.getPackageId();
    }

    @Override
    public String getBookId() {
        return request.getBookId();
    }
}
