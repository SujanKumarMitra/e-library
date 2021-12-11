package com.github.sujankumarmitra.ebookprocessor.v1.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author skmitra
 * @since Dec 11/12/21, 2021
 */
@Data
@ConfigurationProperties
public class DefaultRemoteService extends RemoteService {
    private String id;
    private String baseUrl;
}
