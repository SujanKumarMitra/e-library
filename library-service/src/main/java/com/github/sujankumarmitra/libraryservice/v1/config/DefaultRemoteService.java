package com.github.sujankumarmitra.libraryservice.v1.config;

import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;

/**
 * @author skmitra
 * @since Dec 11/12/21, 2021
 */
@Data
@Validated
public class DefaultRemoteService extends RemoteService {
    @NotEmpty
    private String id;
    @NotEmpty
    private String baseUrl;
}
