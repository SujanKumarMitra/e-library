package com.github.sujankumarmitra.ebookprocessor.v1.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Positive;

/**
 * @author skmitra
 * @since Dec 11/12/21, 2021
 */
@Data
@ConfigurationProperties("app.ebook-processor")
@Validated
public class DefaultEBookProcessorProperties extends EBookProcessorProperties {
    @Positive
    private int threadPoolCapacity;
    private int maxSegmentSize;
}
