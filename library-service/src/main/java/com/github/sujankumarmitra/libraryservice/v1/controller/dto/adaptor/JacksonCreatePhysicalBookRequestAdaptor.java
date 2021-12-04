package com.github.sujankumarmitra.libraryservice.v1.controller.dto.adaptor;

import com.github.sujankumarmitra.libraryservice.v1.controller.dto.JacksonCreatePhysicalBookRequest;
import com.github.sujankumarmitra.libraryservice.v1.controller.dto.JacksonMoney;
import com.github.sujankumarmitra.libraryservice.v1.model.PhysicalBook;
import lombok.NonNull;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author skmitra
 * @since Dec 03/12/21, 2021
 */
public class JacksonCreatePhysicalBookRequestAdaptor extends PhysicalBook {
    private final JacksonCreatePhysicalBookRequest request;

    public JacksonCreatePhysicalBookRequestAdaptor(@NonNull JacksonCreatePhysicalBookRequest request) {
        this.request = request;
    }

    @Override
    public String getId() {
        return null;
    }

    @Override
    public String getTitle() {
        return request.getTitle();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Set<JacksonCreateAuthorRequestAdaptor> getAuthors() {
        return request
                .getAuthors()
                .stream()
                .map(JacksonCreateAuthorRequestAdaptor::new)
                .collect(Collectors.toSet());
    }

    @Override
    public String getPublisher() {
        return request.getPublisher();
    }

    @Override
    public String getEdition() {
        return request.getEdition();
    }

    @Override
    public String getCoverPageImageAssetId() {
        return request.getCoverPageImageAssetId();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Set<JacksonCreateBookTagRequestAdaptor> getTags() {
        return request.getTags() == null ? null : request
                .getTags()
                .stream()
                .map(JacksonCreateBookTagRequestAdaptor::new)
                .collect(Collectors.toSet());
    }

    @Override
    public Long getCopiesAvailable() {
        return request.getCopiesAvailable();
    }

    @Override
    public JacksonMoney getFinePerDay() {
        return request.getFinePerDay();
    }
}
