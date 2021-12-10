package com.github.sujankumarmitra.libraryservice.v1.config;

/**
 * @author skmitra
 * @since Dec 10/12/21, 2021
 */
public abstract class ServiceRegistry {
    public abstract RemoteService getService(String serviceId);
}
