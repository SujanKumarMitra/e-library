package com.github.sujankumarmitra.libraryservice.v1.model;

/**
 * @author skmitra
 * @since Nov 20/11/21, 2021
 */
public abstract class PhysicalBook extends Book {

    public abstract long getCopiesAvailable();

    public abstract Money getFinePerDay();

}
