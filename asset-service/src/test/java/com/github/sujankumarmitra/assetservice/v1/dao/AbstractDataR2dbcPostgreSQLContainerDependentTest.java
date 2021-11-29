package com.github.sujankumarmitra.assetservice.v1.dao;

import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * @author skmitra
 * @since Nov 28/11/21, 2021
 */
@DataR2dbcTest
@Testcontainers
@DirtiesContext
abstract class AbstractDataR2dbcPostgreSQLContainerDependentTest {

    @Container
    protected static final PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres");

    @DynamicPropertySource
    protected static void registerDynamicProperties(DynamicPropertyRegistry registry) {

        registry.add("spring.r2dbc.url", () ->
                postgreSQLContainer.getJdbcUrl().replace("jdbc", "r2dbc"));
        registry.add("spring.r2dbc.username", postgreSQLContainer::getUsername);
        registry.add("spring.r2dbc.password", postgreSQLContainer::getPassword);

    }
}
