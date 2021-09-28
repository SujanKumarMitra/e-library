package com.github.sujankumarmitra.assetservice.v1.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 *
 * For property value evaluation
 *
 * @author skmitra
 * @since Sep 28/09/21, 2021
 */
@Component
public class DefaultMongoProperties {

    @Value("${DB_URL}")
    private String uri;

}
