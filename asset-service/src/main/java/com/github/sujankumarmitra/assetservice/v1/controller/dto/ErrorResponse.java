package com.github.sujankumarmitra.assetservice.v1.controller.dto;

import com.github.sujankumarmitra.assetservice.v1.exception.ErrorDetails;
import com.github.sujankumarmitra.assetservice.v1.openapi.schema.ErrorDetailsSchema;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NonNull;

import java.util.Collection;

/**
 * @author skmitra
 * @since Sep 27/09/21, 2021
 */
@Data
@Schema(
        name = "ErrorResponseSchema",
        description = "Response body in case any error occurs"
)
public class ErrorResponse {
    @ArraySchema(schema = @Schema(implementation = ErrorDetailsSchema.class))
    @NonNull
    private final Collection<ErrorDetails> errors;

    @Schema(title = "total count of errors")
    public int getErrorCount() {
        return errors.size();
    }
}
