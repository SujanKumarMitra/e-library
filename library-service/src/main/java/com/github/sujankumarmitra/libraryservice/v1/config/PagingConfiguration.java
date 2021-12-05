package com.github.sujankumarmitra.libraryservice.v1.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author skmitra
 * @since Dec 05/12/21, 2021
 */
@Configuration
@Slf4j
@EnableConfigurationProperties(DefaultPagingProperties.class)
public class PagingConfiguration implements InitializingBean {

    @Autowired
    private PagingProperties pagingProperties;

    @Override
    public void afterPropertiesSet() {
      log.info("Using {}", pagingProperties);
    }
}
