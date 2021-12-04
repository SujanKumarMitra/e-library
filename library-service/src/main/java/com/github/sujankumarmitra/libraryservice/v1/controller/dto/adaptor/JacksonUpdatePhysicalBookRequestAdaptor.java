package com.github.sujankumarmitra.libraryservice.v1.controller.dto.adaptor;

import com.github.sujankumarmitra.libraryservice.v1.controller.dto.JacksonMoney;
import com.github.sujankumarmitra.libraryservice.v1.controller.dto.JacksonUpdateAuthorRequest;
import com.github.sujankumarmitra.libraryservice.v1.controller.dto.JacksonUpdateBookTagRequest;
import com.github.sujankumarmitra.libraryservice.v1.controller.dto.JacksonUpdatePhysicalBookRequest;
import com.github.sujankumarmitra.libraryservice.v1.model.PhysicalBook;
import lombok.AllArgsConstructor;
import lombok.NonNull;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author skmitra
 * @since Dec 03/12/21, 2021
 */
@AllArgsConstructor
public class JacksonUpdatePhysicalBookRequestAdaptor extends PhysicalBook {
    @NonNull
    private final JacksonUpdatePhysicalBookRequest request;

    @Override
    public String getId() {
        return request.getId();
    }

    @Override
    public String getTitle() {
        return request.getTitle();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Set<JacksonUpdateAuthorRequestAdaptor> getAuthors() {
        Set<JacksonUpdateAuthorRequest> authors = request.getAuthors();
        return authors == null ? null : authors
                .stream()
                .map(JacksonUpdateAuthorRequestAdaptor::new)
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
    public Set<JacksonUpdateBookTagRequestAdaptor> getTags() {
        Set<JacksonUpdateBookTagRequest> tags = request
                .getTags();
        return tags == null ? null : tags
                .stream()
                .map(JacksonUpdateBookTagRequestAdaptor::new)
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
