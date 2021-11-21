package com.github.sujankumarmitra.libraryservice.v1.model;

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
        return getKey().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null) return false;
        if (!Tag.class.isAssignableFrom(obj.getClass())) return false;

        Tag other = Tag.class.cast(obj);
        return getKey().equals(other.getKey()) &&
                getValue().equals(other.getValue());
    }
}
