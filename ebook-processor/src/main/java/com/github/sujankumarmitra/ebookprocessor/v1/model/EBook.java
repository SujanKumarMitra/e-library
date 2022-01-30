package com.github.sujankumarmitra.ebookprocessor.v1.model;

import java.util.Objects;

/**
 * @author skmitra
 * @since Dec 11/12/21, 2021
 */
public abstract class EBook {

    public abstract String getId();

    public abstract String getLibraryId();

    public abstract EBookFormat getFormat();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EBook)) return false;
        EBook response = (EBook) o;
        return Objects.equals(getId(), response.getId()) &&
                Objects.equals(getLibraryId(), response.getLibraryId()) &&
                getFormat() == response.getFormat();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getLibraryId(), getFormat());
    }
}
