package com.github.sujankumarmitra.notificationservice.v1.service.scheduler;

import org.springframework.stereotype.Component;
import reactor.core.Disposable;
import reactor.core.scheduler.Scheduler;

import java.util.concurrent.TimeUnit;

import static java.lang.Integer.MAX_VALUE;
import static java.lang.Runtime.getRuntime;
import static reactor.core.scheduler.Schedulers.newBoundedElastic;

/**
 * @author skmitra
 * @since Nov 13/11/21, 2021
 */
@Component
public class ReactorSchedulerJobScheduler implements JobScheduler {

    private final Scheduler scheduler;

    public ReactorSchedulerJobScheduler() {
        this(newBoundedElastic(
                10 * getRuntime().availableProcessors(),
                MAX_VALUE,
                "job-scheduler"));
    }

    public ReactorSchedulerJobScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    @Override
    public Cancellable scheduleJob(Runnable job, long delay, TimeUnit unit) {
        Disposable disposable = scheduler.schedule(job, delay, unit);
        return disposable::dispose;
    }
}
