package com.github.sujankumarmitra.libraryservice.v1.model;

import java.util.Set;

/**
 * @author skmitra
 * @since Nov 20/11/21, 2021
 */
public abstract class Book {

    public abstract String getId();

    public abstract String getTitle();

    public abstract Set<? extends Author> getAuthors();

    public abstract String getPublisher();

    public abstract String getEdition();

    public abstract Set<? extends Tag> getTags();

    @Override
    public String toString() {
        return "Book{" +
                "id=" + getId() +
                ", title='" + getTitle() + '\'' +
                ", authors=" + getAuthors() +
                ", publisher='" + getPublisher() + '\'' +
                ", edition='" + getEdition() + '\'' +
                ", tags=" + getTags() +
                '}';
    }
}
