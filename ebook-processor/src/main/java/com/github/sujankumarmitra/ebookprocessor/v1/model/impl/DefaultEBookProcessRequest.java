package com.github.sujankumarmitra.ebookprocessor.v1.model.impl;

import com.github.sujankumarmitra.ebookprocessor.v1.model.EBookProcessRequest;
import com.github.sujankumarmitra.ebookprocessor.v1.security.AuthenticationToken;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.core.io.buffer.DataBuffer;
import reactor.core.publisher.Flux;

/**
 * @author skmitra
 * @since Dec 11/12/21, 2021
 */
@Getter
@AllArgsConstructor
public class DefaultEBookProcessRequest extends EBookProcessRequest {

    private final String eBookId;
    private final AuthenticationToken token;
    private final Flux<DataBuffer> eBookBufferFlux;
}
