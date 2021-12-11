package com.github.sujankumarmitra.ebookprocessor.v1.model;

import com.github.sujankumarmitra.ebookprocessor.v1.security.AuthenticationToken;
import org.springframework.core.io.buffer.DataBuffer;
import reactor.core.publisher.Flux;

/**
 * @author skmitra
 * @since Dec 11/12/21, 2021
 */
public abstract class EBookProcessRequest {

    public abstract String getEBookId();

    public abstract AuthenticationToken getToken();

    public abstract Flux<DataBuffer> getEBookBufferFlux();
}
