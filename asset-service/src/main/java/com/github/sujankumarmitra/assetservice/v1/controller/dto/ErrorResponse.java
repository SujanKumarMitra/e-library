package com.github.sujankumarmitra.assetservice.v1.controller.dto;

import com.github.sujankumarmitra.assetservice.v1.exception.ErrorDetails;
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
        name = "ErrorResponse",
        description = "Response body in case any error occurs"
)
public class ErrorResponse {
    @Schema
    @NonNull
    private final Collection<ErrorDetails> errors;

    @Schema
    public int getErrorCount() {
        return errors.size();
    }
}
