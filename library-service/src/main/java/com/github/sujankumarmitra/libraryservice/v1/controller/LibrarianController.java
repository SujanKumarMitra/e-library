package com.github.sujankumarmitra.libraryservice.v1.controller;

import com.github.sujankumarmitra.libraryservice.v1.config.OpenApiConfiguration.ApiAcceptedResponse;
import com.github.sujankumarmitra.libraryservice.v1.config.OpenApiConfiguration.ApiBadRequestResponse;
import com.github.sujankumarmitra.libraryservice.v1.config.OpenApiConfiguration.ApiSecurityScheme;
import com.github.sujankumarmitra.libraryservice.v1.model.Librarian;
import com.github.sujankumarmitra.libraryservice.v1.model.impl.DefaultLibrarian;
import com.github.sujankumarmitra.libraryservice.v1.service.LibrarianService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;

import static com.github.sujankumarmitra.libraryservice.v1.config.OpenApiConfiguration.ApiSecurityResponse;

/**
 * @author skmitra
 * @since Dec 09/12/21, 2021
 */
@RestController
@RequestMapping("/api/librarians")
@AllArgsConstructor
@Tag(
        name = "LibrarianController",
        description = "Controller for managing Librarians"
)
@ApiSecurityScheme
@ApiSecurityResponse
public class LibrarianController {

    @NotNull
    private final LibrarianService librarianService;

    @Operation(
            summary = "Create a Librarian",
            description = "Librarian ids are used to dispatch notifications when Lease Requests are being made." +
                    "<br> Admins can invoke this api."
    )
    @ApiAcceptedResponse
    @ApiBadRequestResponse
    @PutMapping("/{libraryId}/{userId}")
    public Mono<ResponseEntity<Void>> createLibrarian(@PathVariable String userId, @PathVariable String libraryId) {
        Librarian librarian = new DefaultLibrarian(userId, libraryId);
        return librarianService
                .addLibrarian(librarian)
                .then(Mono.fromSupplier(() -> ResponseEntity.accepted().build()));
    }

    @Operation(
            summary = "Delete a librarian",
            description = "Delete a librarian with given id. " +
                    "<br> Admins can invoke this api"
    )
    @ApiAcceptedResponse
    @DeleteMapping("/{libraryId}/{userId}")
    public Mono<ResponseEntity<Void>> deleteLibrarian(@PathVariable String userId, @PathVariable String libraryId) {
        Librarian librarian = new DefaultLibrarian(userId, libraryId);
        return librarianService
                .deleteLibrarian(librarian)
                .then(Mono.fromSupplier(() -> ResponseEntity.accepted().build()));
    }

}
