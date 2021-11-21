package com.github.sujankumarmitra.libraryservice.v1.model;

/**
 * @author skmitra
 * @since Nov 20/11/21, 2021
 */
public abstract class AcceptedLease {

    public abstract String getLeaseRequestId();

    public abstract long getStartTime();

    public abstract long getEndTime();

}
