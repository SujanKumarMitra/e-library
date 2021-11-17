package com.github.sujankumarmitra.assetservice;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.testcontainers.containers.GenericContainer;

import java.util.List;

/**
 * @author skmitra
 * @since Nov 16/11/21, 2021
 */
public class TestcontainersExtension implements BeforeAllCallback, AfterAllCallback {

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        Class<?> aClass = context.getTestClass().orElseGet(() -> null);
        if (aClass == null) return;

        List<GenericContainer<?>> containers = (List<GenericContainer<?>>) aClass
                .getDeclaredMethod("getManagedContainers")
                .invoke(null);
        containers.forEach(GenericContainer::start);

    }

    @Override
    public void afterAll(ExtensionContext context) throws Exception {
        Class<?> aClass = context.getTestClass().orElseGet(() -> null);
        if (aClass == null) return;

        List<GenericContainer<?>> containers = (List<GenericContainer<?>>) aClass
                .getDeclaredMethod("getManagedContainers")
                .invoke(null);
        containers.forEach(GenericContainer::stop);
    }
}
