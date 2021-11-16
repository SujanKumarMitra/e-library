package com.github.sujankumarmitra.notificationservice.v1.config;

import com.github.sujankumarmitra.notificationservice.v1.service.scheduler.JobScheduler;
import com.github.sujankumarmitra.notificationservice.v1.service.scheduler.ReactorSchedulerJobScheduler;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.scheduler.Schedulers;

import static com.github.sujankumarmitra.notificationservice.v1.config.JobSchedulerProperties.DEFAULT_THREAD_CAPACITY;

/**
 * @author skmitra
 * @since Nov 16/11/21, 2021
 */
@Configuration
@EnableConfigurationProperties(DefaultJobSchedulerProperties.class)
@AllArgsConstructor
@Slf4j
public class JobSchedulerConfiguration implements InitializingBean {

    private final JobSchedulerProperties jobSchedulerProperties;

    @Bean
    public JobScheduler jobScheduler() {
        int maxThreadCap = jobSchedulerProperties.getMaxThreadCapacity();

        if (maxThreadCap == DEFAULT_THREAD_CAPACITY)
            maxThreadCap = Runtime.getRuntime().availableProcessors();

        return new ReactorSchedulerJobScheduler(
                Schedulers.newBoundedElastic(
                        maxThreadCap,
                        Integer.MAX_VALUE,
                        "job-scheduler"));
    }


    @Override
    public void afterPropertiesSet() {
        log.info("Using {}", jobSchedulerProperties);
    }
}
