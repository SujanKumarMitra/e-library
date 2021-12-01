package com.github.sujankumarmitra.libraryservice.v1.controller;

import com.github.sujankumarmitra.libraryservice.v1.config.OpenApiConfiguration.ApiNotFoundResponse;
import com.github.sujankumarmitra.libraryservice.v1.model.RejectedLease;
import com.github.sujankumarmitra.libraryservice.v1.openapi.schema.GetRejectedLeaseRequestResponseSchema;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * @author skmitra
 * @since Dec 01/12/21, 2021
 */
@RestController
@RequestMapping("/api/v1/lease-requests/rejected")
@Tag(
        name = "RejectedLeaseRequestController",
        description = "### Controller for rejected lease requests"
)
public class RejectedLeaseRequestController {

    @Operation(
            summary = "Fetch rejected lease by leaseRequestId",
            description = "Students will invoke this api, to see the reason for a lease request rejection")
    @ApiResponse(
            responseCode = "200",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = GetRejectedLeaseRequestResponseSchema.class)
            )
    )
    @ApiNotFoundResponse
    @GetMapping("/{leaseRequestId}")
    public Mono<ResponseEntity<RejectedLease>> getRejectedLeaseById(@PathVariable String leaseRequestId) {
        return Mono.empty();
    }
}