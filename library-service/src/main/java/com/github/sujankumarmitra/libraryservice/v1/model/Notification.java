package com.github.sujankumarmitra.libraryservice.v1.model;

/**
 * @author skmitra
 * @since Nov 20/11/21, 2021
 */
public abstract class Notification {

    public abstract String getConsumerId();

    public abstract String getPayload();

    public abstract long getTimestamp();

}
