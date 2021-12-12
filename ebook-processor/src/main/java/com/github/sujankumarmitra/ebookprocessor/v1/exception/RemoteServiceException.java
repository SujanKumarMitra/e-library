package com.github.sujankumarmitra.ebookprocessor.v1.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Collection;
import java.util.List;

/**
 * @author skmitra
 * @since Dec 12/12/21, 2021
 */
@Getter
@AllArgsConstructor
public class RemoteServiceException extends ApiOperationException {
    public final HttpStatus status;
    private final String message;

    @Override
    public Collection<ErrorDetails> getErrors() {
        return List.of(
                new DefaultErrorDetails("Remote Service communication failed! " +
                        "status='" + status + "' message='" + message + "'"));
    }
}
