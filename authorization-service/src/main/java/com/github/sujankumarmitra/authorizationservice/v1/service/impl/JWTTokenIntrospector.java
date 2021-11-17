package com.github.sujankumarmitra.authorizationservice.v1.service.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.github.sujankumarmitra.authorizationservice.v1.model.TokenIntrospectionRequest;
import com.github.sujankumarmitra.authorizationservice.v1.model.TokenIntrospectionResponse;
import com.github.sujankumarmitra.authorizationservice.v1.model.impl.DefaultTokenIntrospectionResponse;
import com.github.sujankumarmitra.authorizationservice.v1.service.TokenIntrospector;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static java.util.Optional.empty;

/**
 * @author skmitra
 * @since Nov 17/11/21, 2021
 */
@Service
@Slf4j
public class JWTTokenIntrospector implements TokenIntrospector {

    private static final TokenIntrospectionResponse INVALID_TOKEN_RESPONSE;

    public static final String SUBJECT_CLAIM_KEY = "sub";
    public static final String SCOPES_CLAIM_KEY = "scopes";
    public static final String NOT_BEFORE_CLAIM_KEY = "nbf";
    public static final String EXPIRY_CLAIM_KEY = "exp";

    static {
        INVALID_TOKEN_RESPONSE = DefaultTokenIntrospectionResponse
                .newBuilder()
                .valid(false)
                .subject(empty())
                .scopes(empty())
                .expiry(empty())
                .notBefore(empty())
                .build();
    }


    @Override
    public Mono<TokenIntrospectionResponse> introspect(@NonNull TokenIntrospectionRequest request) {
        return Mono.defer(() -> Mono.just(doIntrospect(request)));
    }


    private TokenIntrospectionResponse doIntrospect(TokenIntrospectionRequest request) {

        String token = request.getToken();
        if (token == null) {
            log.debug("token is null, returning invalid response");
            return INVALID_TOKEN_RESPONSE;
        }

        try {
            DecodedJWT decode = JWT.decode(token);
            if (decode == null) {
                log.debug("JWT.decode() returned null, returning invalid token");
                return INVALID_TOKEN_RESPONSE;
            }

            String subject = decode.getClaim(SUBJECT_CLAIM_KEY).asString();
            List<String> scopes = decode.getClaim(SCOPES_CLAIM_KEY).asList(String.class);
            Long notBefore = decode.getClaim(NOT_BEFORE_CLAIM_KEY).asLong();
            Long expiry = decode.getClaim(EXPIRY_CLAIM_KEY).asLong();

            boolean hasNulls = Stream
                    .of(subject, scopes, notBefore, expiry)
                    .anyMatch(Objects::isNull);

            if (hasNulls) {
                log.debug("All required fields are not present in JWT, returning invalid response");
                return INVALID_TOKEN_RESPONSE;
            }

            return DefaultTokenIntrospectionResponse
                    .newBuilder()
                    .valid(true)
                    .subject(subject)
                    .scopes(scopes)
                    .expiry(expiry)
                    .notBefore(notBefore)
                    .build();

        } catch (JWTDecodeException ex) {
            log.debug("Exception thrown while decoding jwt, returning invalid response", ex);
            return INVALID_TOKEN_RESPONSE;
        }
    }
}
