package com.github.sujankumarmitra.libraryservice.v1.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author skmitra
 * @since Dec 10/12/21, 2021
 */
@Data
@Validated
@ConfigurationProperties(prefix = "app")
public class EnvironmentServiceRegistry extends ServiceRegistry {
    private Map<String, DefaultRemoteService> remoteServices = Collections.synchronizedMap(new HashMap<>());

    @Override
    public RemoteService getService(String serviceId) {
        return remoteServices.get(serviceId);
    }
}
