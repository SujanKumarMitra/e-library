package com.github.sujankumarmitra.libraryservice.v1.controller.dto.adaptor;

import com.github.sujankumarmitra.libraryservice.v1.controller.dto.JacksonUpdateAuthorRequest;
import com.github.sujankumarmitra.libraryservice.v1.controller.dto.JacksonUpdateBookTagRequest;
import com.github.sujankumarmitra.libraryservice.v1.controller.dto.JacksonUpdateEBookRequest;
import com.github.sujankumarmitra.libraryservice.v1.model.EBook;
import com.github.sujankumarmitra.libraryservice.v1.model.EBookFormat;
import lombok.Getter;
import lombok.NonNull;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author skmitra
 * @since Dec 03/12/21, 2021
 */
@Getter
public class JacksonUpdateEBookRequestAdaptor extends EBook {
    private final JacksonUpdateEBookRequest request;

    public JacksonUpdateEBookRequestAdaptor(@NonNull JacksonUpdateEBookRequest request) {
        this.request = request;
    }

    @Override
    public String getId() {
        return request.getId();
    }

    @Override
    public String getTitle() {
        return request.getTitle();
    }

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

    @Override
    public Set<JacksonUpdateBookTagRequestAdaptor> getTags() {
        Set<JacksonUpdateBookTagRequest> tags = request.getTags();
        return tags == null ? null : tags
                .stream()
                .map(JacksonUpdateBookTagRequestAdaptor::new)
                .collect(Collectors.toSet());
    }

    @Override
    public EBookFormat getFormat() {
        return request.getFormat();
    }
}
