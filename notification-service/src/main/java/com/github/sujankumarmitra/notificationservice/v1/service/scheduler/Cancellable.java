package com.github.sujankumarmitra.notificationservice.v1.service.scheduler;

/**
 * @author skmitra
 * @since Nov 13/11/21, 2021
 */
@FunctionalInterface
public interface Cancellable {

    void cancel();
}
