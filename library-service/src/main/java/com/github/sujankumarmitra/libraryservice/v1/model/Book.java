package com.github.sujankumarmitra.libraryservice.v1.model;

import java.util.Objects;
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

    public abstract String getCoverPageImageId();

    public abstract Set<? extends Tag> getTags();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Book)) return false;
        Book book = (Book) o;
        return Objects.equals(getId(), book.getId()) &&
                Objects.equals(getTitle(), book.getTitle()) &&
                Objects.equals(getAuthors(), book.getAuthors()) &&
                Objects.equals(getPublisher(), book.getPublisher()) &&
                Objects.equals(getEdition(), book.getEdition()) &&
                Objects.equals(getCoverPageImageId(), book.getCoverPageImageId()) &&
                Objects.equals(getTags(), book.getTags());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(),
                getTitle(),
                getAuthors(),
                getPublisher(),
                getEdition(),
                getCoverPageImageId(),
                getTags());
    }

    @Override
    public String toString() {
        return "Book{" +
                "id=" + getId() +
                ", title='" + getTitle() + '\'' +
                ", authors=" + getAuthors() +
                ", publisher='" + getPublisher() + '\'' +
                ", edition='" + getEdition() + '\'' +
                ", coverPageImageId='" + getCoverPageImageId() + '\'' +
                ", tags=" + getTags() +
                '}';
    }
}