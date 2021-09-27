package com.github.sujankumarmitra.assetservice.v1.controller;

import com.github.sujankumarmitra.assetservice.v1.config.ApiSecurityScheme;
import com.github.sujankumarmitra.assetservice.v1.model.StoredAsset;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static io.swagger.v3.oas.annotations.enums.ParameterIn.PATH;
import static java.lang.String.format;
import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_OCTET_STREAM_VALUE;

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
@ApiSecurityScheme
public class AssetStorageController {

    public static final String CONTENT_DISPOSITION_FORMAT = "attachment; filename=\"%s\"";
    @NonNull
    private final AssetStorageService assetStorageService;

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
                    content = @Content(
                            mediaType = "application/octet-stream",
                            schema = @Schema(
                                    description = "a stream of bytes",
                                    implementation = byte[].class,
                                    format = "binary"
                            )
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Server has successfully handled the request"
                    )
            }
    )
    @PutMapping(value = "/assets/{assetId}", consumes = {APPLICATION_OCTET_STREAM_VALUE})
    @PreAuthorize("hasAuthority('WRITE_ASSET')")
    public Mono<ResponseEntity<Object>> storeAsset(@PathVariable String assetId, ServerWebExchange exchange) {
        Flux<DataBuffer> dataBuffers = exchange.getRequest().getBody();

        return assetStorageService
                .storeAsset(assetId, dataBuffers)
                .map(__ -> ResponseEntity.ok().build())
                .onErrorResume(ControllerUtils::translateErrors);

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
                    )
            }
    )
    @GetMapping("/assets/{assetId}")
    @PreAuthorize("hasAuthority('READ_ASSET')")
    public Mono<ResponseEntity<InputStreamSource>> retrieveAsset(Authentication authenticatedUser,
                                                                 @PathVariable String assetId) {
        return assetStorageService.retrieveAsset(assetId)
                .map(this::toResponseEntity);
    }

    private ResponseEntity<InputStreamSource> toResponseEntity(StoredAsset storedAsset) {
        String assetName = storedAsset.getAsset().getName();
        InputStreamSource streamSource = storedAsset.getInputStreamSource();
        return ResponseEntity
                .ok()
                .header(CONTENT_DISPOSITION, format(CONTENT_DISPOSITION_FORMAT, assetName))
                .header(CONTENT_TYPE, APPLICATION_OCTET_STREAM_VALUE)
                .body(streamSource);
    }
}
