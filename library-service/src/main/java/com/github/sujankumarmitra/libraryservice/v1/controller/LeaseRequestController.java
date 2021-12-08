package com.github.sujankumarmitra.libraryservice.v1.controller;

import com.github.sujankumarmitra.libraryservice.v1.config.OpenApiConfiguration.ApiAcceptedResponse;
import com.github.sujankumarmitra.libraryservice.v1.config.OpenApiConfiguration.ApiBadRequestResponse;
import com.github.sujankumarmitra.libraryservice.v1.config.OpenApiConfiguration.ApiConflictResponse;
import com.github.sujankumarmitra.libraryservice.v1.config.OpenApiConfiguration.ApiCreatedResponse;
import com.github.sujankumarmitra.libraryservice.v1.controller.dto.*;
import com.github.sujankumarmitra.libraryservice.v1.exception.ApiOperationException;
import com.github.sujankumarmitra.libraryservice.v1.model.AcceptedLease;
import com.github.sujankumarmitra.libraryservice.v1.model.LeaseRequest;
import com.github.sujankumarmitra.libraryservice.v1.model.LeaseStatus;
import com.github.sujankumarmitra.libraryservice.v1.model.RejectedLease;
import com.github.sujankumarmitra.libraryservice.v1.openapi.schema.AcceptLeaseRequestRequestSchema;
import com.github.sujankumarmitra.libraryservice.v1.openapi.schema.CreateLeaseRequestRequestSchema;
import com.github.sujankumarmitra.libraryservice.v1.openapi.schema.GetPendingLeaseRequestResponseSchema;
import com.github.sujankumarmitra.libraryservice.v1.openapi.schema.RejectLeaseRequestRequestSchema;
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
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.net.URI;

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
    @GetMapping("/pending")
    public Flux<LeaseRequest> getPendingLeases(@RequestParam(name = "page_no", defaultValue = "0") int pageNo) {
        return leaseRequestService.getPendingLeaseRequests(pageNo);
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
    @GetMapping("/pending/self")
//    public Flux<LeaseRequest> getAllPendingLeasesForCurrentUser(@RequestParam(name = "page_no", defaultValue = "0") int pageNo) {
    public Flux<LeaseRequest> getAllPendingLeasesForCurrentUser(@RequestParam(name = "page_no", defaultValue = "0") int pageNo, @RequestParam String userId) {
//        String userId = ""; // TODO Spring Security Authentication.getName()
        return leaseRequestService
                .getPendingLeaseRequests(userId, pageNo);
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
    public Mono<ResponseEntity<Void>> createLeaseRequest(@RequestBody JacksonValidCreateLeaseRequest request, @RequestParam String userId) {
//        String userId = ""; // TODO Spring Security Authentication.getName()

        request.setUserId(userId);
        request.setStatus(PENDING);
        request.setTimestamp(System.currentTimeMillis());

        System.out.println(request);
        return leaseRequestService
                .createLeaseRequest(request)
                .map(URI::create)
                .map(location -> ResponseEntity.created(location).build());
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
    @PatchMapping("/{leaseRequestId}")
    public Mono<ResponseEntity<Object>> handleLeaseRequest(@PathVariable String leaseRequestId,
                                                         @RequestBody @Valid JacksonValidHandleLeaseRequestRequest request) {

        request.setLeaseRequestId(leaseRequestId);
        LeaseStatus status = request.getStatus();

        Mono<ResponseEntity<Object>> completionMono;

        if (status == ACCEPTED) {
            AcceptedLease acceptedLease = new JacksonValidAcceptLeaseRequestRequestAdaptor((JacksonValidAcceptLeaseRequestRequest) request);
            System.out.println(acceptedLease);
            completionMono = leaseRequestService
                    .acceptLeaseRequest(acceptedLease)
                    .then(Mono.fromSupplier(() -> accepted().build()));

        } else if (status == REJECTED) {
            RejectedLease rejectedLease = new JacksonValidRejectLeaseRequestRequestAdaptor((JacksonValidRejectLeaseRequestRequest) request);
            System.out.println(rejectedLease);
            completionMono = leaseRequestService
                    .rejectLeaseRequest(rejectedLease)
                    .then(Mono.fromSupplier(() -> ResponseEntity.accepted().build()));
        } else {
            // this should not happen
            completionMono = Mono.error(new RuntimeException("could not determine request type"));
        }
        return completionMono
                .onErrorResume(ApiOperationException.class,
                        err -> Mono.fromSupplier(() -> ResponseEntity.status(CONFLICT).body(err.getErrors())));
    }

    @Operation(
            summary = "Deletes a lease request",
            description = "Students will invoke this API to cancel a lease request.")
    @ApiAcceptedResponse
    @DeleteMapping("/{leaseRequestId}")
    public Mono<ResponseEntity<Void>> deleteLeaseRequest(@PathVariable String leaseRequestId) {

        System.out.println(leaseRequestId);

        return leaseRequestService
                .deleteLeaseRequest(leaseRequestId)
                .then(Mono.fromSupplier(accepted()::build));
    }

}
