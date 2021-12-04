package com.github.sujankumarmitra.libraryservice.v1.controller.dto.adaptor;

import com.github.sujankumarmitra.libraryservice.v1.controller.dto.JacksonUpdatePackageRequest;
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
public class JacksonUpdatePackageRequestAdaptor extends Package {
    @NonNull
    private final JacksonUpdatePackageRequest request;

    @Override
    public String getId() {
        return request.getId();
    }

    @Override
    public String getName() {
        return request.getName();
    }

    @Override
    public Set<JacksonUpdatePackageItemRequestAdaptor> getItems() {
        return request.getItems() == null ? null : request
                .getItems()
                .stream()
                .map(JacksonUpdatePackageItemRequestAdaptor::new)
                .collect(Collectors.toSet());
    }

    @Override
    public Set<JacksonUpdatePackageTagRequestAdaptor> getTags() {
        return request.getTags() == null ? null : request
                .getTags()
                .stream()
                .map(JacksonUpdatePackageTagRequestAdaptor::new)
                .collect(Collectors.toSet());
    }
}
