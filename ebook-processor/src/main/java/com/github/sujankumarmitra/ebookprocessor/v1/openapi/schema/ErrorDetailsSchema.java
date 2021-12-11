package com.github.sujankumarmitra.ebookprocessor.v1.openapi.schema;

import com.github.sujankumarmitra.ebookprocessor.v1.exception.ErrorDetails;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * @author skmitra
 * @since Dec 01/12/21, 2021
 */
@Schema(description = "Represents an error")
public class ErrorDetailsSchema extends ErrorDetails {
    @Override
    @Schema(description = "human readable message")
    public String getMessage() {
        return null;
    }
}
