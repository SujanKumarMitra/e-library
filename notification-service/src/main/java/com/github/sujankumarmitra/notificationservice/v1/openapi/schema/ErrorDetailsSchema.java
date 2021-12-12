package com.github.sujankumarmitra.notificationservice.v1.openapi.schema;

import com.github.sujankumarmitra.notificationservice.v1.exception.ErrorDetails;

import javax.validation.constraints.NotEmpty;

/**
 * @author skmitra
 * @since Dec 01/12/21, 2021
 */
public class ErrorDetailsSchema extends ErrorDetails {
    @Override
    @NotEmpty
    public String getMessage() {
        return null;
    }
}
