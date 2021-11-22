package com.github.sujankumarmitra.libraryservice.v1.model;

import java.util.Objects;

/**
 * @author skmitra
 * @since Nov 21/11/21, 2021
 */
public abstract class Author {

    public abstract String getBookId();

    public abstract String getName();

    @Override
    public boolean equals(Object o) {
        if(o == null) return false;
        if (!Author.class.isAssignableFrom(o.getClass()))
            return false;

        Author other = (Author) o;
        return Objects.equals(getBookId(), other.getBookId()) &&
                Objects.equals(getName(), other.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getBookId(), getName());
    }

}
