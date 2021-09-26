package com.github.sujankumarmitra.assetservice.v1.controller;

import com.github.sujankumarmitra.assetservice.v1.controller.dto.GrantPermissionRequest;
import com.github.sujankumarmitra.assetservice.v1.service.AssetPermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import static org.springframework.http.ResponseEntity.ok;

/**
 * @author skmitra
 * @since Sep 26/09/21, 2021
 */
@RestController
@RequestMapping("/v1/asset/permission")
@AllArgsConstructor
@Tag(
        name = "AssetPermissionController",
        description = "### Controller for granting clients permission to access assets"
)
public class AssetPermissionController {

    @NonNull
    private final AssetPermissionService permissionService;

    @Operation(
            description = "# Grant permission to a client to access a asset",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Server has acknowledged the request"
                    )
            }
    )
    @PutMapping("/")
    public Mono<ResponseEntity<Object>> grantPermission(@RequestBody GrantPermissionRequest permission) {
        return permissionService
                .grantPermission(permission)
                .map(__ -> ok().build())
                .onErrorResume(ControllerUtils::translateErrors);
    }
}