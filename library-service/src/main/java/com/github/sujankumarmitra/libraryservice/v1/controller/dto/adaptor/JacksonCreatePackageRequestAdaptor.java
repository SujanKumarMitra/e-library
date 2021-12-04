package com.github.sujankumarmitra.libraryservice.v1.controller.dto.adaptor;

import com.github.sujankumarmitra.libraryservice.v1.controller.dto.JacksonCreatePackageRequest;
import com.github.sujankumarmitra.libraryservice.v1.model.Package;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author skmitra
 * @since Dec 04/12/21, 2021
 */
@Getter
@Setter
@AllArgsConstructor
public class JacksonCreatePackageRequestAdaptor extends Package {
    @NonNull
    private final JacksonCreatePackageRequest request;

    @Override
    public String getId() {
        return null;
    }

    @Override
    public String getName() {
        return request.getName();
    }

    @Override
    public Set<JacksonCreatePackageItemRequestAdaptor> getItems() {
        return request
                .getItems()
                .stream()
                .map(JacksonCreatePackageItemRequestAdaptor::new)
                .collect(Collectors.toSet());
    }

    @Override
    public Set<JacksonCreatePackageTagRequestAdaptor> getTags() {
        return request.getTags() == null ? null : request
                .getTags()
                .stream()
                .map(JacksonCreatePackageTagRequestAdaptor::new)
                .collect(Collectors.toSet());
    }
}
