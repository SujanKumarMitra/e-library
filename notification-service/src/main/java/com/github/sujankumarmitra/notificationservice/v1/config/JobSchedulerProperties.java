package com.github.sujankumarmitra.notificationservice.v1.config;

/**
 * @author skmitra
 * @since Nov 16/11/21, 2021
 */
public abstract class JobSchedulerProperties {

    /**
     * {@link Runtime#availableProcessors()} from {@link Runtime#getRuntime()}
     * will be used
     */
    public static final int DEFAULT_THREAD_CAPACITY = -1;

    public abstract int getMaxThreadCapacity();
}
