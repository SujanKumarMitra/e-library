package com.github.sujankumarmitra.notificationservice.v1.service.scheduler;

import reactor.core.Disposable;
import reactor.core.scheduler.Scheduler;

import java.util.concurrent.TimeUnit;

/**
 * @author skmitra
 * @since Nov 13/11/21, 2021
 */
public class ReactorSchedulerJobScheduler implements JobScheduler {

    private final Scheduler scheduler;

    public ReactorSchedulerJobScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    @Override
    public Cancellable scheduleJob(Runnable job, long delay, TimeUnit unit) {
        Disposable disposable = scheduler.schedule(job, delay, unit);
        return disposable::dispose;
    }
}
