package com.github.sujankumarmitra.libraryservice.v1.model;

import java.util.Objects;

/**
 * @author skmitra
 * @since Nov 20/11/21, 2021
 */
public abstract class EBook extends Book {

    public abstract EBookFormat getFormat();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EBook)) return false;
        if (!super.equals(o)) return false;

        EBook book = (EBook) o;
        return getFormat() == book.getFormat();
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                super.hashCode(),
                getFormat());
    }

    @Override
    public String toString() {
        return "EBook{" +
                "id='" + getId() + '\'' +
                ", title='" + getTitle() + '\'' +
                ", authors=" + getAuthors() +
                ", publisher='" + getPublisher() + '\'' +
                ", edition='" + getEdition() + '\'' +
                ", coverPageImageAssetId='" + getCoverPageImageAssetId() + '\'' +
                ", tags=" + getTitle() +
                ", format=" + getFormat() +
                '}';
    }

}
