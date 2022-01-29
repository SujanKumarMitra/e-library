package com.github.sujankumarmitra.libraryservice.v1.model;

import java.util.Objects;

/**
 * @author skmitra
 * @since Nov 21/11/21, 2021
 */
public abstract class BookAuthor {

    public abstract String getId();

    public abstract String getBookId();

    public abstract String getName();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BookAuthor)) return false;

        BookAuthor other = (BookAuthor) o;
        return Objects.equals(getId(), other.getId()) &&
                Objects.equals(getBookId(), other.getBookId()) &&
                Objects.equals(getName(), other.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                getId(),
                getBookId(),
                getName());
    }

    @Override
    public String toString() {
        return "BookAuthor{" +
                "id='" + getId() + '\'' +
                ", bookId='" + getBookId() + '\'' +
                ", name='" + getName() + '\'' +
                '}';
    }


}
