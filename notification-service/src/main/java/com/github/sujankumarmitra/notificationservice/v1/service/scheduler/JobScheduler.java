package com.github.sujankumarmitra.notificationservice.v1.service.scheduler;

import java.util.concurrent.TimeUnit;

/**
 * @author skmitra
 * @since Nov 13/11/21, 2021
 */
public interface JobScheduler {

    Cancellable scheduleJob(Runnable job, long delay, TimeUnit unit);
}
