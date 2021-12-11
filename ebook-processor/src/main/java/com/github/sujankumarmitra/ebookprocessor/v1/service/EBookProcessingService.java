package com.github.sujankumarmitra.ebookprocessor.v1.service;

import com.github.sujankumarmitra.ebookprocessor.v1.model.EBookProcessRequest;
import reactor.core.publisher.Mono;

/**
 * @author skmitra
 * @since Dec 11/12/21, 2021
 */
public interface EBookProcessingService {

    Mono<String> submitProcess(EBookProcessRequest processRequest);
}
