package com.github.sujankumarmitra.libraryservice.v1.controller;

import com.github.sujankumarmitra.libraryservice.v1.config.OpenApiConfiguration.ApiAcceptedResponse;
import com.github.sujankumarmitra.libraryservice.v1.config.OpenApiConfiguration.ApiNotFoundResponse;
import com.github.sujankumarmitra.libraryservice.v1.exception.LeaseRequestNotFoundException;
import com.github.sujankumarmitra.libraryservice.v1.model.LeaseRecord;
import com.github.sujankumarmitra.libraryservice.v1.model.Money;
import com.github.sujankumarmitra.libraryservice.v1.openapi.schema.GetActiveLeaseRequestResponseSchema;
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
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;

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
//    public Flux<LeaseRecord> getActiveLeasesForCurrentUser(@RequestParam(value = "page_no", defaultValue = "0") int pageNo) {
    public Flux<LeaseRecord> getActiveLeasesForCurrentUser(@RequestParam(value = "page_no", defaultValue = "0") int pageNo, @RequestParam String userId) {
//        String userId = ""; // TODO Spring Security Authentication.getName()
        return activeLeaseService.getAllActiveLeases(userId, pageNo);
    }

    @Operation(
            summary = "Fetch fine amount for an active lease",
            description = "Librarians will invoke this api to see the fine amount of a lease. " +
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
    @PatchMapping("/{leaseRequestId}/relinquish")
    public Mono<ResponseEntity<Void>> relinquishActiveLease(@PathVariable String leaseRequestId) {
        return activeLeaseService
                .relinquishActiveLease(leaseRequestId)
                .then(Mono.fromSupplier(() -> ResponseEntity.accepted().build()));
    }

    @Operation(
            summary = "Trigger system to invalidate expired ebook leases",
            description = "The system is expected to automatically relinquish active ebook leases, once they expire." +
                    "<br> But, sometimes this operation might take some time to activate, due to multiple reasons like scheduler delay, clock drift etc." +
                    "<br> So, <b>Librarians</b> can manually trigger relinquishing of expired ebook leases." +
                    "<br><b> Note: auto relinquishment of leases only occurs for ebooks, not for physical books"
    )
    @ApiAcceptedResponse
    @PutMapping("/invalidate")
    public Mono<ResponseEntity<Void>> invalidateStaleEBookLeases() {
        return activeLeaseService
                .invalidateStateEBookLeases()
                .then(Mono.fromSupplier(() -> ResponseEntity.accepted().build()));
    }


}
