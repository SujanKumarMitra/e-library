package com.github.sujankumarmitra.libraryservice.v1.controller.dto.adaptor;

import com.github.sujankumarmitra.libraryservice.v1.controller.dto.JacksonCreateEBookRequest;
import com.github.sujankumarmitra.libraryservice.v1.model.EBook;
import com.github.sujankumarmitra.libraryservice.v1.model.EBookFormat;
import lombok.Getter;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author skmitra
 * @since Dec 03/12/21, 2021
 */
@Getter
public class JacksonCreateEBookRequestAdaptor extends EBook {
    private final JacksonCreateEBookRequest request;

    public JacksonCreateEBookRequestAdaptor(JacksonCreateEBookRequest request) {
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

    @Override
    public Set<JacksonCreateBookTagRequestAdaptor> getTags() {
        return request
                .getTags()
                .stream()
                .map(JacksonCreateBookTagRequestAdaptor::new)
                .collect(Collectors.toSet());
    }

    @Override
    public EBookFormat getFormat() {
        return request.getFormat();
    }
}
