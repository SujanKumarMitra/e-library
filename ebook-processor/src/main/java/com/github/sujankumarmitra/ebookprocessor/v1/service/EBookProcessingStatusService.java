package com.github.sujankumarmitra.ebookprocessor.v1.service;

import com.github.sujankumarmitra.ebookprocessor.v1.model.EBookProcessingStatus;
import reactor.core.publisher.Mono;

/**
 * @author skmitra
 * @since Dec 11/12/21, 2021
 */
public interface EBookProcessingStatusService {

    Mono<EBookProcessingStatus> getStatus(String processId);

    Mono<Void> saveStatus(EBookProcessingStatus processingStatus);
}
