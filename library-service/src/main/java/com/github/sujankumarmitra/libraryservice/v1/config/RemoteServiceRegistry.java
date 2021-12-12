package com.github.sujankumarmitra.libraryservice.v1.config;

/**
 * @author skmitra
 * @since Dec 11/12/21, 2021
 */
public abstract class RemoteServiceRegistry {

    public abstract RemoteService getService(String serviceId);
}
