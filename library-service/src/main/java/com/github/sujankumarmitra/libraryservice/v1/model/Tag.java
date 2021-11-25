package com.github.sujankumarmitra.libraryservice.v1.model;

import java.util.Objects;

/**
 * @author skmitra
 * @since Nov 20/11/21, 2021
 */
public abstract class Tag {

    public abstract String getId();

    public abstract String getBookId();

    public abstract String getKey();

    public abstract String getValue();

    @Override
    public int hashCode() {
        return Objects.hash(
                getId(),
                getBookId(),
                getKey(),
                getValue());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Tag)) return false;

        Tag other = (Tag) obj;
        return Objects.equals(getId(), other.getId()) &&
                Objects.equals(getBookId(), other.getBookId()) &&
                Objects.equals(getKey(), other.getKey()) &&
                Objects.equals(getValue(), other.getValue());
    }

    @Override
    public String toString() {
        return "Tag{" +
                "id='" + getId() + '\'' +
                ", bookId='" + getBookId() + '\'' +
                ", key='" + getKey() + '\'' +
                ", value='" + getValue() + '\'' +
                '}';
    }
}
