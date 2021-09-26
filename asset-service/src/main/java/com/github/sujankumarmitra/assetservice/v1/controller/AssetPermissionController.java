package com.github.sujankumarmitra.assetservice.v1.controller;

import com.github.sujankumarmitra.assetservice.v1.model.AssetPermission;
import com.github.sujankumarmitra.assetservice.v1.service.AssetPermissionService;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import static org.springframework.http.ResponseEntity.accepted;

/**
 * @author skmitra
 * @since Sep 26/09/21, 2021
 */
@RestController
@RequestMapping("/v1/asset/permission")
@AllArgsConstructor
public class AssetPermissionController {

    @NonNull
    private final AssetPermissionService permissionService;

    @PutMapping("/")
    public Mono<ResponseEntity<Object>> grantPermission(AssetPermission permission) {
        return permissionService
                .grantPermission(permission)
                .map(__ -> accepted().build())
                .onErrorResume(ControllerUtils::translateErrors);
    }
}