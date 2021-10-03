package com.github.sujankumarmitra.notificationservice.v1.security;

import org.springframework.security.core.AuthenticationException;

/**
 * @author skmitra
 * @since Sep 27/09/21, 2021
 */
public class TokenValidationException extends AuthenticationException {

    public TokenValidationException(String reason) {
        super(reason);
    }
}
