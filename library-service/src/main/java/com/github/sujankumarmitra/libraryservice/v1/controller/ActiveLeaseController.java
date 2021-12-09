package com.github.sujankumarmitra.libraryservice.v1.controller;

import com.github.sujankumarmitra.libraryservice.v1.config.OpenApiConfiguration.*;
import com.github.sujankumarmitra.libraryservice.v1.controller.dto.ErrorResponse;
import com.github.sujankumarmitra.libraryservice.v1.exception.ApiOperationException;
import com.github.sujankumarmitra.libraryservice.v1.exception.LeaseRequestNotFoundException;
import com.github.sujankumarmitra.libraryservice.v1.model.LeaseRecord;
import com.github.sujankumarmitra.libraryservice.v1.model.Money;
import com.github.sujankumarmitra.libraryservice.v1.openapi.schema.GetActiveLeaseRequestResponseSchema;
import com.github.sujankumarmitra.libraryservice.v1.openapi.schema.MoneySchema;
import com.github.sujankumarmitra.libraryservice.v1.security.SecurityAnnotations.RoleLibrarian;
import com.github.sujankumarmitra.libraryservice.v1.security.SecurityAnnotations.RoleStudent;
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
@RequestMapping("/api/v1/lease-requests/active")
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
            description = "Librarian will invoke this api to see all currently active leases")
    @ApiResponse(
            responseCode = "200",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    array = @ArraySchema(
                            schema = @Schema(implementation = GetActiveLeaseRequestResponseSchema.class)
                    )
            )
    )
    @GetMapping
    @RoleLibrarian
    public Flux<LeaseRecord> getActiveLeases(@RequestParam(value = "page_no", defaultValue = "0") int pageNo) {
        return activeLeaseService.getAllActiveLeases(pageNo);
    }

    @Operation(
            summary = "Fetch active leases for currently authenticated user",
            description = " Students will invoke this api to see their currently active leases." +
                    "<br> StudentId is taken from JWT \"sub\" claim")
    @ApiResponse(
            responseCode = "200",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    array = @ArraySchema(
                            schema = @Schema(implementation = GetActiveLeaseRequestResponseSchema.class)
                    )
            )
    )
    @GetMapping("/self")
    @RoleStudent
    public Flux<LeaseRecord> getActiveLeasesForCurrentUser(@RequestParam(value = "page_no", defaultValue = "0") int pageNo, Authentication authentication) {
        return activeLeaseService.getAllActiveLeases(authentication.getName(), pageNo);
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
    @RoleLibrarian
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
    @RoleLibrarian
    @PatchMapping("/{leaseRequestId}/relinquish")
    public Mono<ResponseEntity<Object>> relinquishActiveLease(@PathVariable String leaseRequestId) {
        return activeLeaseService
                .relinquishActiveLease(leaseRequestId)
                .then(Mono.fromSupplier(() -> ResponseEntity.accepted().build()))
                .onErrorResume(ApiOperationException.class,
                        err -> Mono.fromSupplier(() -> ResponseEntity.status(CONFLICT).body(new ErrorResponse(err.getErrors()))));
    }

    @Operation(
            summary = "Trigger system to invalidate expired ebook leases",
            description = "The system is expected to automatically relinquish active ebook leases, once they expire." +
                    "<br> But, sometimes this operation might take some time to activate, due to multiple reasons like scheduler delay, clock drift etc." +
                    "<br> So, <b>Librarians</b> can manually trigger relinquishing of expired ebook leases." +
                    "<br><b> Note: auto relinquishment of leases only occurs for ebooks, not for physical books"
    )
    @ApiAcceptedResponse
    @RoleLibrarian
    @PutMapping("/invalidate")
    public Mono<ResponseEntity<Void>> invalidateStaleEBookLeases() {
        return activeLeaseService
                .invalidateStateEBookLeases()
                .then(Mono.fromSupplier(() -> ResponseEntity.accepted().build()));
    }


}
