package com.github.sujankumarmitra.notificationservice.v1.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author skmitra
 * @since Nov 16/11/21, 2021
 */
@Data
@ConfigurationProperties("app.job-scheduler")
public class DefaultJobSchedulerProperties extends JobSchedulerProperties {
    private int maxThreadCapacity = DEFAULT_THREAD_CAPACITY;
}
