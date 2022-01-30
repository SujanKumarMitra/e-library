package com.github.sujankumarmitra.libraryservice.v1.controller;

import com.github.sujankumarmitra.libraryservice.v1.config.OpenApiConfiguration.*;
import com.github.sujankumarmitra.libraryservice.v1.controller.dto.ErrorResponse;
import com.github.sujankumarmitra.libraryservice.v1.exception.ApiOperationException;
import com.github.sujankumarmitra.libraryservice.v1.exception.LeaseRequestNotFoundException;
import com.github.sujankumarmitra.libraryservice.v1.model.AcceptedLease;
import com.github.sujankumarmitra.libraryservice.v1.model.Money;
import com.github.sujankumarmitra.libraryservice.v1.openapi.schema.GetActiveAcceptedLeaseRequestResponseSchema;
import com.github.sujankumarmitra.libraryservice.v1.openapi.schema.MoneySchema;
import com.github.sujankumarmitra.libraryservice.v1.service.ActiveLeaseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;

import static org.springframework.http.HttpStatus.CONFLICT;

/**
 * @author skmitra
 * @since Dec 02/12/21, 2021
 */
@RestController
@RequestMapping("/api/lease-requests/active")
@AllArgsConstructor
@Tag(
        name = "ActiveLeaseController",
        description = "Controller for active book leases requests"
)
@ApiSecurityScheme
@ApiSecurityResponse
public class ActiveLeaseController {

    @NotNull
    private final ActiveLeaseService activeLeaseService;

    @Operation(
            summary = "Fetch all active leases",
            description = "Librarian can invoke this api to see all currently active leases")
    @ApiResponse(
            responseCode = "200",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    array = @ArraySchema(
                            schema = @Schema(implementation = GetActiveAcceptedLeaseRequestResponseSchema.class)
                    )
            )
    )
    @GetMapping
    public Flux<AcceptedLease> getActiveLeases(
            @RequestParam("library_id") String libraryId,
            @RequestParam(value = "page_no", defaultValue = "0") int pageNo) {
        return activeLeaseService.getAllActiveLeases(libraryId, pageNo);
    }

    @Operation(
            summary = "Fetch active leases for currently authenticated user",
            description = " Students can invoke this api to see their currently active leases.")
    @ApiResponse(
            responseCode = "200",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    array = @ArraySchema(
                            schema = @Schema(implementation = GetActiveAcceptedLeaseRequestResponseSchema.class)
                    )
            )
    )
    @GetMapping("/self")
    public Flux<AcceptedLease> getActiveLeasesForCurrentUser(
            @RequestParam("library_id") String libraryId,
            @RequestParam(value = "page_no", defaultValue = "0") int pageNo,
            Authentication authentication) {
        return activeLeaseService
                .getAllActiveLeases(libraryId, authentication.getName(), pageNo);
    }

    @Operation(
            summary = "Fetch fine amount for an active lease",
            description = "Librarians/Students will invoke this api to see the fine amount of a lease. " +
                    "<br> The API is valid for PhysicalBook leases only")
    @ApiNotFoundResponse
    @ApiResponse(
            responseCode = "200",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = MoneySchema.class)
            )
    )
    @GetMapping("/{leaseRequestId}/fine")
    public Mono<ResponseEntity<Money>> getFineForActiveLease(@PathVariable String leaseRequestId) {
        return activeLeaseService
                .getFineForActiveLease(leaseRequestId)
                .map(ResponseEntity::ok)
                .onErrorResume(LeaseRequestNotFoundException.class, err ->
                        Mono.fromSupplier(() -> ResponseEntity.notFound().build()));

    }

    @Operation(
            summary = "Relinquish an active lease",
            description = "Librarians will invoke this api to relinquish an active lease." +
                    "<br>This api is valid for PhysicalBook leases only." +
                    "<br> For EBook leases, the system will automatically relinquish the lease, when current time" +
                    "becomes greater than leaseEndTime")
    @ApiAcceptedResponse
    @ApiConflictResponse
    @PatchMapping("/{leaseRequestId}/relinquish")
    public Mono<ResponseEntity<Object>> relinquishActiveLease(@PathVariable String leaseRequestId) {
        return activeLeaseService
                .relinquishActiveLease(leaseRequestId)
                .then(Mono.fromSupplier(() -> ResponseEntity.accepted().build()))
                .onErrorResume(ApiOperationException.class,
                        err -> Mono.fromSupplier(() -> ResponseEntity.status(CONFLICT).body(new ErrorResponse(err.getErrors()))));
    }


}
