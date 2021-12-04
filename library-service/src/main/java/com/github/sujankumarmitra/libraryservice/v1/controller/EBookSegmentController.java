package com.github.sujankumarmitra.libraryservice.v1.controller;

import com.github.sujankumarmitra.libraryservice.v1.config.OpenApiConfiguration.ApiNotFoundResponse;
import com.github.sujankumarmitra.libraryservice.v1.model.EBookSegment;
import com.github.sujankumarmitra.libraryservice.v1.openapi.schema.GetEBookSegmentResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author skmitra
 * @since Dec 02/12/21, 2021
 */
@RestController
@RequestMapping("/api/v1/books/{bookId}/segments")
@Tag(
        name = "EBookSegmentController",
        description = "### Controller for ebook segments"
)
public class EBookSegmentController {

    @Operation(
            summary = "Fetch all segments of an ebook",
            description = "Librarians/Teachers/Students will invoke this API"
    )
    @GetMapping
    @ApiResponse(
            responseCode = "200",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    array = @ArraySchema(
                            schema = @Schema(implementation = GetEBookSegmentResponse.class)
                    )
            )
    )
    public Flux<EBookSegment> getAllSegments(@PathVariable String bookId) {
        return Flux.empty();
    }

    @GetMapping("/{segmentIndex}")
    @ApiResponse(
            responseCode = "200",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = GetEBookSegmentResponse.class))
    )
    @ApiNotFoundResponse
    public Mono<EBookSegment> getSegmentByIndex(@PathVariable String bookId,
                                                @PathVariable long segmentIndex) {
        return Mono.empty();
    }

}
