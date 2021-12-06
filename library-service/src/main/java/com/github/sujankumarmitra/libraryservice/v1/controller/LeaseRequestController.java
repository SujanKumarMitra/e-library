package com.github.sujankumarmitra.libraryservice.v1.controller;

import com.github.sujankumarmitra.libraryservice.v1.config.OpenApiConfiguration.ApiAcceptedResponse;
import com.github.sujankumarmitra.libraryservice.v1.config.OpenApiConfiguration.ApiBadRequestResponse;
import com.github.sujankumarmitra.libraryservice.v1.config.OpenApiConfiguration.ApiConflictResponse;
import com.github.sujankumarmitra.libraryservice.v1.controller.dto.ErrorResponse;
import com.github.sujankumarmitra.libraryservice.v1.model.LeaseRequest;
import com.github.sujankumarmitra.libraryservice.v1.openapi.schema.AcceptLeaseRequestRequestSchema;
import com.github.sujankumarmitra.libraryservice.v1.openapi.schema.CreateLeaseRequestRequestSchema;
import com.github.sujankumarmitra.libraryservice.v1.openapi.schema.GetPendingLeaseRequestResponseSchema;
import com.github.sujankumarmitra.libraryservice.v1.openapi.schema.RejectLeaseRequestRequestSchema;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author skmitra
 * @since Dec 01/12/21, 2021
 */
@RestController
@RequestMapping("/api/v1/lease-requests")
@Tag(
        name = "LeaseRequestController",
        description = "Controller for managing book leases requests"
)
public class LeaseRequestController {


    @Operation(
            summary = "Fetch all pending lease requests",
            description = " Librarians will invoke this API to fetch all pending lease requests"
    )
    @ApiResponse(
            responseCode = "200",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    array = @ArraySchema(
                            schema = @Schema(implementation = GetPendingLeaseRequestResponseSchema.class)
                    )
            )
    )
    @GetMapping("/pending")
    public Flux<LeaseRequest> getAllPendingLeases(@RequestParam(name = "page_no", defaultValue = "0") long pageNo) {
        return Flux.empty();
    }

    @Operation(
            summary = "Fetch all pending lease requests for currently authenticated user",
            description = " Students will invoke this API to see their currently pending lease requests" +
                    "<br> Student id is taken from JWT sub claim")
    @ApiResponse(
            responseCode = "200",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    array = @ArraySchema(
                            schema = @Schema(implementation = GetPendingLeaseRequestResponseSchema.class)
                    )
            )
    )
    @GetMapping("/pending/self")
    public Flux<LeaseRequest> getAllPendingLeasesForCurrentUser(@RequestParam(name = "page_no", defaultValue = "0") long pageNo) {
        return Flux.empty();
    }

    @Operation(
            summary = "Create a lease request",
            description = " Students will invoke this api to create a lease request" +
                    "<br> Student id is taken from JWT sub claim")
    @ApiResponse(
            responseCode = "201",
            headers = @Header(
                    name = "Location",
                    description = "Unique ID pointing to this lease request",
                    schema = @Schema(
                            example = "7d553b6b-c6e4-42a7-bc8d-7cda07909b2f"
                    )
            )
    )
    @ApiBadRequestResponse
    @ApiConflictResponse
    @PostMapping
    public Mono<ResponseEntity<Void>> createLeaseRequest(CreateLeaseRequestRequestSchema request) {
        return Mono.empty();
    }


    @Operation(
            summary = "Accept/Reject a lease request",
            description = " Librarians will invoke this API to either accept or reject a leaseRequest." +
                    "<br> Please refer the request body to see the payload type",
            requestBody = @RequestBody(
                    content = @Content(
                            schema = @Schema(
                                    oneOf = {AcceptLeaseRequestRequestSchema.class, RejectLeaseRequestRequestSchema.class}
                            )
                    )
            )
    )
    @ApiResponse(
            responseCode = "400",
            description = "payload is inconsistent",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ErrorResponse.class)
            )
    )
    @ApiAcceptedResponse
    @ApiConflictResponse
    @PatchMapping("/{leaseRequestId}")
    public Mono<ResponseEntity<Void>> handleLeaseRequest(@PathVariable String leaseRequestId) {
        return Mono.empty();
    }


    @Operation(
            summary = "Deletes a lease request",
            description = " Students will invoke this API to cancel a lease request.")
    @ApiAcceptedResponse
    @DeleteMapping("/{leaseRequestId}")
    public Mono<ResponseEntity<Void>> deleteLeaseRequest(@PathVariable String leaseRequestId) {
        return Mono.empty();
    }

}
