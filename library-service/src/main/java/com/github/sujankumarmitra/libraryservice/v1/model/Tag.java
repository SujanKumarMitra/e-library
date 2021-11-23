package com.github.sujankumarmitra.libraryservice.v1.model;

import java.util.Objects;

/**
 * @author skmitra
 * @since Nov 20/11/21, 2021
 */
public abstract class Tag {

    public abstract String getBookId();

    public abstract String getKey();

    public abstract String getValue();

    @Override
    public int hashCode() {
        return Objects.hash(getBookId(), getKey());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (!Tag.class.isAssignableFrom(obj.getClass())) return false;

        Tag other = Tag.class.cast(obj);
        return Objects.equals(getBookId(), other.getBookId()) &&
                Objects.equals(getKey(), other.getKey()) &&
                Objects.equals(getValue(), other.getValue());
    }

    @Override
    public String toString() {
        return "Tag{" +
                "bookId=" + getBookId() +
                ", key='" + getKey() + '\'' +
                ", value='" + getValue() + '\'' +
                '}';
    }
}
