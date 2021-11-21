package com.github.sujankumarmitra.libraryservice.v1.model;

import java.util.Set;

/**
 * @author skmitra
 * @since Nov 20/11/21, 2021
 */
public abstract class Book {

    public abstract String getId();

    public abstract String getName();

    public abstract Set<Author> getAuthors();

    public abstract String getPublisher();

    public abstract String getEdition();

    public abstract Set<Tag> getTags();

}
