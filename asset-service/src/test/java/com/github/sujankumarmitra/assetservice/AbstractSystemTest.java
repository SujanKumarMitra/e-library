package com.github.sujankumarmitra.assetservice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.r2dbc.core.ConnectionAccessor;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * @author skmitra
 * @since Dec 10/12/21, 2021
 */
@SpringBootTest
@ActiveProfiles("test")
@Testcontainers
@Slf4j
@DirtiesContext
public abstract class AbstractSystemTest {

    @Container
    protected static final PostgreSQLContainer<?> POSTGRESQL_CONTAINER;
    @Autowired
    protected ConnectionAccessor connectionAccessor;

    static {
        POSTGRESQL_CONTAINER = new PostgreSQLContainer<>("postgres");
    }

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.r2dbc.url", () -> POSTGRESQL_CONTAINER.getJdbcUrl().replace("jdbc", "r2dbc"));
        registry.add("spring.r2dbc.username", POSTGRESQL_CONTAINER::getUsername);
        registry.add("spring.r2dbc.password", POSTGRESQL_CONTAINER::getPassword);

    }

}
