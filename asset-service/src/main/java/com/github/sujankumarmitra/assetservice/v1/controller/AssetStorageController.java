package com.github.sujankumarmitra.assetservice.v1.controller;

import com.github.sujankumarmitra.assetservice.v1.controller.dto.ErrorResponse;
import com.github.sujankumarmitra.assetservice.v1.exception.AssetNeverStoredException;
import com.github.sujankumarmitra.assetservice.v1.exception.AssetNotFoundException;
import com.github.sujankumarmitra.assetservice.v1.exception.DefaultErrorDetails;
import com.github.sujankumarmitra.assetservice.v1.model.StoredAsset;
import com.github.sujankumarmitra.assetservice.v1.service.AssetPermissionService;
import com.github.sujankumarmitra.assetservice.v1.service.AssetStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.core.io.InputStreamSource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static com.github.sujankumarmitra.assetservice.v1.config.OpenApiConfiguration.ApiBadRequestResponse;
import static com.github.sujankumarmitra.assetservice.v1.config.OpenApiConfiguration.ApiSecurityScheme;
import static io.swagger.v3.oas.annotations.enums.ParameterIn.PATH;
import static java.lang.String.format;
import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.PRECONDITION_REQUIRED;
import static org.springframework.http.MediaType.APPLICATION_OCTET_STREAM_VALUE;
import static org.springframework.http.ResponseEntity.ok;
import static org.springframework.http.ResponseEntity.status;

/**
 * @author skmitra
 * @since Sep 24/09/21, 2021
 */
@RestController
@RequestMapping("/api/v1")
@AllArgsConstructor
@Tag(
        name = "AssetStorageController",
        description = "### Controller for storing and retrieving asset objects"
)
//@OpenApiConfiguration.ApiSecurityResponse
@ApiSecurityScheme
public class AssetStorageController {

    public static final String CONTENT_DISPOSITION_FORMAT = "attachment; filename=\"%s\"";
    @NonNull
    private final AssetStorageService assetStorageService;
    @NonNull
    private final AssetPermissionService assetPermissionService;

    @Operation(
            description = "# Upload a binary object to a associated Asset",
            parameters = {
                    @Parameter(
                            name = "assetId",
                            in = PATH,
                            description = "Unique ID of asset"
                    )
            },
            requestBody = @RequestBody(
                    description = "a stream of bytes",
                    content = {
                            @Content(
                                    mediaType = "application/octet-stream",
                                    schema = @Schema(
                                            description = "a stream of bytes",
                                            implementation = byte[].class,
                                            format = "binary"
                                    )
                            )

                    }
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Server has successfully handled the request"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Asset with provided assetId not found"
                    )
            }
    )
    @PutMapping(value = "/assets/{assetId}", consumes = {APPLICATION_OCTET_STREAM_VALUE})
    @PreAuthorize("hasAuthority('WRITE_ASSET')")
    @ApiBadRequestResponse
    public Mono<ResponseEntity<Void>> storeAsset(@PathVariable String assetId, ServerWebExchange exchange) {
        Flux<DataBuffer> dataBuffers = exchange.getRequest().getBody();

        return assetStorageService
                .storeAsset(assetId, dataBuffers)
                .thenReturn(ok().build());

    }

    @Operation(
            description = "# Download the binary object to a associated Asset",
            parameters = {
                    @Parameter(
                            name = "assetId",
                            in = PATH,
                            description = "Unique ID of asset"
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Server has successfully handled the request",
                            headers = {
                                    @Header(
                                            name = "Content-Disposition",
                                            description = "value=attachment; filename={assetName}"
                                    )
                            },
                            content = {
                                    @Content(
                                            mediaType = "application/octet-stream",
                                            schema = @Schema(
                                                    description = "a stream of bytes",
                                                    implementation = byte[].class,
                                                    format = "binary"
                                            )
                                    )

                            }
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Asset with provided assetId not found",
                            content = @Content(
                                    schema = @Schema
                            )
                    ),
                    @ApiResponse(
                            responseCode = "428",
                            description = "asset is created but never stored",
                            content = @Content(
                                    schema = @Schema
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "client does not have permission to access the asset",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = ErrorResponse.class,
                                            format = "json"
                                    )
                            )
                    )
            }
    )
    @GetMapping("/assets/{assetId}")
    @PreAuthorize("hasAuthority('READ_ASSET')")
    public Mono<ResponseEntity<InputStreamSource>> retrieveAsset(Authentication authenticatedUser,
                                                                 @PathVariable String assetId) {
        return assetPermissionService
                .hasPermission(assetId, authenticatedUser.getName())
                .filter(Boolean::booleanValue)
                .switchIfEmpty(Mono.error(new AccessDeniedException("You don't have permission to access this resource")))
                .flatMap(trueVal -> assetStorageService.retrieveAsset(assetId))
                .map(this::toResponseEntity);
    }


    @ExceptionHandler(AssetNeverStoredException.class)
    public Mono<ResponseEntity<ErrorResponse>> assetNeverStoredExceptionHandler(AssetNeverStoredException ex) {
        return Mono.just(status(PRECONDITION_REQUIRED)
                .body(new ErrorResponse(ex.getErrors())));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public Mono<ResponseEntity<ErrorResponse>> accessDeniedExceptionHandler(AccessDeniedException ex) {
        return Mono.just(status(FORBIDDEN)
                .body(new ErrorResponse(List.of(new DefaultErrorDetails(ex.getMessage())))));
    }

    @ExceptionHandler(AssetNotFoundException.class)
    public Mono<ResponseEntity<ErrorResponse>> assetNotFoundExceptionHandler(AssetNotFoundException ex) {
        return Mono.just(ResponseEntity.notFound().build());
    }

    private ResponseEntity<InputStreamSource> toResponseEntity(StoredAsset storedAsset) {
        String assetName = storedAsset.getAsset().getName();
        InputStreamSource streamSource = storedAsset.getInputStreamSource();
        return ok()
                .header(CONTENT_DISPOSITION, format(CONTENT_DISPOSITION_FORMAT, assetName))
                .header(CONTENT_TYPE, APPLICATION_OCTET_STREAM_VALUE)
                .body(streamSource);
    }
}
