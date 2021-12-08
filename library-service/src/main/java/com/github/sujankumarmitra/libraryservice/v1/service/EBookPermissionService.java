package com.github.sujankumarmitra.libraryservice.v1.service;

import com.github.sujankumarmitra.libraryservice.v1.model.EBookPermission;
import reactor.core.publisher.Mono;

/**
 * @author skmitra
 * @since Dec 07/12/21, 2021
 */
public interface EBookPermissionService {

    Mono<Void> assignPermission(EBookPermission ebookPermission);
}
