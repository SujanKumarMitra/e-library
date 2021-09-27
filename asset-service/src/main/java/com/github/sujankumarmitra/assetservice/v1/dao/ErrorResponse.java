package com.github.sujankumarmitra.assetservice.v1.dao;

import com.github.sujankumarmitra.assetservice.v1.exception.ErrorDetails;
import lombok.Data;

import java.util.Collection;

/**
 * @author skmitra
 * @since Sep 27/09/21, 2021
 */
@Data
public class ErrorResponse {
    private final Collection<ErrorDetails> errors;
}
