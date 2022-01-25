package com.github.sujankumarmitra.libraryservice.v1.model;

import java.util.Objects;

/**
 * @author skmitra
 * @since Nov 20/11/21, 2021
 */
public abstract class PhysicalBook extends Book {

    public abstract Long getCopiesAvailable();

    public abstract Money getFinePerDay();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!super.equals(o)) return false;

        if (!(o instanceof PhysicalBook)) return false;

        PhysicalBook book = (PhysicalBook) o;
        return Objects.equals(getFinePerDay(), book.getFinePerDay()) &&
                Objects.equals(getCopiesAvailable(), book.getCopiesAvailable());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getFinePerDay(), getCopiesAvailable());
    }


    @Override
    public String toString() {
        return "PhysicalBook{" +
                "id='" + getId() + '\'' +
                ", libraryId='" + getLibraryId() + '\'' +
                ", title='" + getTitle() + '\'' +
                ", authors=" + getAuthors() +
                ", publisher='" + getPublisher() + '\'' +
                ", edition='" + getEdition() + '\'' +
                ", coverPageImageAssetId='" + getCoverPageImageAssetId() + '\'' +
                ", tags=" + getTags() +
                ", finePerDay=" + getFinePerDay() +
                ", copiesAvailable=" + getCopiesAvailable() +
                '}';
    }

}
