package com.github.sujankumarmitra.libraryservice.v1.service.impl;

import com.github.sujankumarmitra.libraryservice.v1.model.EBookPermission;
import com.github.sujankumarmitra.libraryservice.v1.service.EBookPermissionService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * @author skmitra
 * @since Dec 07/12/21, 2021
 */
@Service
public class AssetBasedEBookPermissionService implements EBookPermissionService {
    @Override
    public Mono<Void> assignPermission(EBookPermission ebookPermission) {
//      TODO implement me
        return Mono.error(new RuntimeException("not implemented yet!!"));
    }
}
