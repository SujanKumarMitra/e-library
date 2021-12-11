package com.github.sujankumarmitra.ebookprocessor.v1.openapi.schema;

import com.github.sujankumarmitra.ebookprocessor.v1.model.EBookProcessingStatus;
import com.github.sujankumarmitra.ebookprocessor.v1.model.ProcessingState;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import static com.github.sujankumarmitra.ebookprocessor.v1.model.ProcessingState.COMPLETED;

/**
 * @author skmitra
 * @since Dec 11/12/21, 2021
 */
@Schema(description = "Response describing processing status of an ebook")
public class GetProcessingStatusResponseSchema extends EBookProcessingStatus {
    @Override
    @NotEmpty
    @Schema(description = "id of processing")
    public String getProcessId() {
        return null;
    }

    @Override
    @Schema(description = "current state of processing")
    @NotNull
    public ProcessingState getState() {
        return COMPLETED;
    }

    @Schema(description = "reason for current state of processing")
    @NotEmpty
    @Override
    public String getMessage() {
        return null;
    }
}
