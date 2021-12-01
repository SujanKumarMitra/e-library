package com.github.sujankumarmitra.libraryservice.v1.model;

/**
 * @author skmitra
 * @since Nov 20/11/21, 2021
 */
public abstract class LeaseRequest {

    public abstract String getId();

    public abstract String getBookId();

    public abstract String getUserId();

    public abstract LeaseStatus getStatus();

    public abstract long getTimestamp();
}
