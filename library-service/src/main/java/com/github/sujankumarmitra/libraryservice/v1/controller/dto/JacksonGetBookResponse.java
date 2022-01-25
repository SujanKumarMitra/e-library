package com.github.sujankumarmitra.libraryservice.v1.controller.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.sujankumarmitra.libraryservice.v1.model.Author;
import com.github.sujankumarmitra.libraryservice.v1.model.Book;
import com.github.sujankumarmitra.libraryservice.v1.model.BookTag;
import lombok.AllArgsConstructor;
import lombok.NonNull;

import java.util.Set;

/**
 * @author skmitra
 * @since Dec 05/12/21, 2021
 */
@AllArgsConstructor
public abstract class JacksonGetBookResponse extends Book {

    @NonNull
    @JsonIgnore
    private final Book book;

    @Override
    public String getId() {
        return book.getId();
    }

    @Override
    public String getLibraryId() {
        return book.getLibraryId();
    }

    @Override
    public String getTitle() {
        return book.getTitle();
    }

    @Override
    @JsonSerialize(contentAs = Author.class)
    public Set<Author> getAuthors() {
        return book.getAuthors();
    }

    @Override
    public String getPublisher() {
        return book.getPublisher();
    }

    @Override
    public String getEdition() {
        return book.getEdition();
    }

    @Override
    public String getCoverPageImageAssetId() {
        return book.getCoverPageImageAssetId();
    }

    @Override
    @JsonSerialize(contentAs = BookTag.class)
    public Set<BookTag> getTags() {
        return book.getTags();
    }

    public abstract JacksonBookType getType();

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
