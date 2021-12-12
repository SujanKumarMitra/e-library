package com.github.sujankumarmitra.libraryservice.v1.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.validation.annotation.Validated;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author skmitra
 * @since Dec 11/12/21, 2021
 */
@Validated
@ConfigurationProperties("app")
public class EnvironmentRemoteServiceRegistry extends RemoteServiceRegistry {

    private final Map<String, RemoteService> serviceMap;

    @ConstructorBinding
    public EnvironmentRemoteServiceRegistry(List<DefaultRemoteService> remoteServices) {
        serviceMap = new HashMap<>();
        remoteServices.forEach(service -> serviceMap.put(service.getId(), service));
    }

    @Override
    public RemoteService getService(String serviceId) {
        return serviceMap.get(serviceId);
    }

    @Override
    public String toString() {
        return "EnvironmentRemoteServiceRegistry{" +
                "serviceMap=" + serviceMap +
                '}';
    }
}
