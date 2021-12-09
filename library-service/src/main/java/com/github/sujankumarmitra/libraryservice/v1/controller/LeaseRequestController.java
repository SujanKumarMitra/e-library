package com.github.sujankumarmitra.libraryservice.v1.controller;

import com.github.sujankumarmitra.libraryservice.v1.config.OpenApiConfiguration.ApiAcceptedResponse;
import com.github.sujankumarmitra.libraryservice.v1.config.OpenApiConfiguration.ApiBadRequestResponse;
import com.github.sujankumarmitra.libraryservice.v1.config.OpenApiConfiguration.ApiConflictResponse;
import com.github.sujankumarmitra.libraryservice.v1.config.OpenApiConfiguration.ApiCreatedResponse;
import com.github.sujankumarmitra.libraryservice.v1.controller.dto.*;
import com.github.sujankumarmitra.libraryservice.v1.exception.ApiOperationException;
import com.github.sujankumarmitra.libraryservice.v1.model.AcceptedLease;
import com.github.sujankumarmitra.libraryservice.v1.model.LeaseStatus;
import com.github.sujankumarmitra.libraryservice.v1.model.RejectedLease;
import com.github.sujankumarmitra.libraryservice.v1.openapi.schema.AcceptLeaseRequestRequestSchema;
import com.github.sujankumarmitra.libraryservice.v1.openapi.schema.CreateLeaseRequestRequestSchema;
import com.github.sujankumarmitra.libraryservice.v1.openapi.schema.GetPendingLeaseRequestResponseSchema;
import com.github.sujankumarmitra.libraryservice.v1.openapi.schema.RejectLeaseRequestRequestSchema;
import com.github.sujankumarmitra.libraryservice.v1.security.SecurityAnnotations.RoleLibrarian;
import com.github.sujankumarmitra.libraryservice.v1.security.SecurityAnnotations.RoleStudent;
import com.github.sujankumarmitra.libraryservice.v1.service.LeaseRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.net.URI;

import static com.github.sujankumarmitra.libraryservice.v1.config.OpenApiConfiguration.ApiSecurityResponse;
import static com.github.sujankumarmitra.libraryservice.v1.config.OpenApiConfiguration.ApiSecurityScheme;
import static com.github.sujankumarmitra.libraryservice.v1.model.LeaseStatus.*;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.ResponseEntity.accepted;

/**
 * @author skmitra
 * @since Dec 01/12/21, 2021
 */
@RestController
@RequestMapping("/api/v1/lease-requests")
@AllArgsConstructor
@Tag(
        name = "LeaseRequestController",
        description = "Controller for managing book leases requests"
)
@ApiSecurityScheme
@ApiSecurityResponse
public class LeaseRequestController {

    @NonNull
    private final LeaseRequestService leaseRequestService;

    @Operation(
            summary = "Fetch all pending lease requests",
            description = "Librarians will invoke this API to fetch all pending lease requests"
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
    @RoleLibrarian
    @GetMapping("/pending")
    public Flux<JacksonGetPendingLeaseRequestResponse> getPendingLeases(@RequestParam(name = "page_no", defaultValue = "0") int pageNo) {
        return leaseRequestService
                .getPendingLeaseRequests(pageNo)
                .map(JacksonGetPendingLeaseRequestResponse::new);
    }

    @Operation(
            summary = "Fetch all pending lease requests for currently authenticated user",
            description = "Students will invoke this API to see their currently pending lease requests" +
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
    @RoleStudent
    @GetMapping("/pending/self")
    public Flux<JacksonGetPendingLeaseRequestResponse> getAllPendingLeasesForCurrentUser(@RequestParam(name = "page_no", defaultValue = "0") int pageNo, Authentication authentication) {
        return leaseRequestService
                .getPendingLeaseRequests(authentication.getName(), pageNo)
                .map(JacksonGetPendingLeaseRequestResponse::new);
    }

    @Operation(
            summary = "Create a lease request",
            description = "Students will invoke this api to create a lease request" +
                    "<br> Student id is taken from JWT sub claim")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(schema = @Schema(implementation = CreateLeaseRequestRequestSchema.class)
            )
    )
    @ApiCreatedResponse
    @ApiBadRequestResponse
    @ApiConflictResponse
    @PostMapping
    @RoleStudent
    public Mono<ResponseEntity<Object>> createLeaseRequest(@RequestBody JacksonValidCreateLeaseRequest request, Authentication authentication) {
        request.setUserId(authentication.getName());
        request.setStatus(PENDING);
        request.setTimestamp(System.currentTimeMillis());

        return leaseRequestService
                .createLeaseRequest(request)
                .map(URI::create)
                .map(location -> ResponseEntity.created(location).build())
                .onErrorResume(ApiOperationException.class,
                        err -> Mono.fromSupplier(() -> ResponseEntity.status(CONFLICT).body(new ErrorResponse(err.getErrors()))));
    }


    @Operation(
            summary = "Accept/Reject a lease request",
            description = "Librarians will invoke this API to either accept or reject a leaseRequest." +
                    "<br> Please refer the request body to see the payload type")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(schema = @Schema(
                    oneOf = {AcceptLeaseRequestRequestSchema.class, RejectLeaseRequestRequestSchema.class}
            ))
    )
    @ApiBadRequestResponse
    @ApiAcceptedResponse
    @ApiConflictResponse
    @RoleLibrarian
    @PatchMapping("/{leaseRequestId}")
    public Mono<ResponseEntity<Object>> handleLeaseRequest(@PathVariable String leaseRequestId,
                                                           @RequestBody @Valid JacksonValidHandleLeaseRequestRequest request) {

        request.setLeaseRequestId(leaseRequestId);
        LeaseStatus status = request.getStatus();

        Mono<ResponseEntity<Object>> completionMono;

        if (status == ACCEPTED) {
            AcceptedLease acceptedLease = new JacksonValidAcceptLeaseRequestRequestAdaptor((JacksonValidAcceptLeaseRequestRequest) request);
            completionMono = leaseRequestService
                    .acceptLeaseRequest(acceptedLease)
                    .then(Mono.fromSupplier(() -> accepted().build()));

        } else if (status == REJECTED) {
            RejectedLease rejectedLease = new JacksonValidRejectLeaseRequestRequestAdaptor((JacksonValidRejectLeaseRequestRequest) request);
            completionMono = leaseRequestService
                    .rejectLeaseRequest(rejectedLease)
                    .then(Mono.fromSupplier(() -> ResponseEntity.accepted().build()));
        } else {
            // this should not happen
            completionMono = Mono.error(new RuntimeException("could not determine request type"));
        }
        return completionMono
                .onErrorResume(ApiOperationException.class,
                        err -> Mono.fromSupplier(() -> ResponseEntity.status(CONFLICT).body(new ErrorResponse(err.getErrors()))));
    }

    @Operation(
            summary = "Deletes a lease request",
            description = "Students will invoke this API to cancel a lease request.")
    @ApiAcceptedResponse
    @RoleStudent
    @DeleteMapping("/{leaseRequestId}")
    public Mono<ResponseEntity<Void>> deleteLeaseRequest(@PathVariable String leaseRequestId) {

        return leaseRequestService
                .deleteLeaseRequest(leaseRequestId)
                .then(Mono.fromSupplier(accepted()::build));
    }

}
