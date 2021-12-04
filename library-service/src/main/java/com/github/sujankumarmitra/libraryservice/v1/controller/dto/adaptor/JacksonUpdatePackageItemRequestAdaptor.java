package com.github.sujankumarmitra.libraryservice.v1.controller.dto.adaptor;

import com.github.sujankumarmitra.libraryservice.v1.controller.dto.JacksonUpdatePackageItemRequest;
import com.github.sujankumarmitra.libraryservice.v1.model.PackageItem;
import lombok.AllArgsConstructor;
import lombok.NonNull;

/**
 * @author skmitra
 * @since Dec 04/12/21, 2021
 */
@AllArgsConstructor
public class JacksonUpdatePackageItemRequestAdaptor extends PackageItem {
    @NonNull
    private final JacksonUpdatePackageItemRequest request;

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
