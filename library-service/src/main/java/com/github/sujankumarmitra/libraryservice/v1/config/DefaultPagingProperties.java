package com.github.sujankumarmitra.libraryservice.v1.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author skmitra
 * @since Dec 05/12/21, 2021
 */
@Data
@ConfigurationProperties("app.paging")
public class DefaultPagingProperties extends PagingProperties {
    private int defaultPageSize = 5;
}
