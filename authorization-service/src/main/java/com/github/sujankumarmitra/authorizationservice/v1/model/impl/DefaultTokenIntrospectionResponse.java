package com.github.sujankumarmitra.authorizationservice.v1.model.impl;

import com.github.sujankumarmitra.authorizationservice.v1.model.TokenIntrospectionResponse;
import lombok.Getter;
import lombok.NonNull;

import java.util.Collection;
import java.util.Optional;

import static java.util.Optional.ofNullable;

/**
 * @author skmitra
 * @since Nov 17/11/21, 2021
 */

@Getter
public class DefaultTokenIntrospectionResponse extends TokenIntrospectionResponse {
    private boolean active;

    @NonNull
    private final Optional<String> subject;

    @NonNull
    private final Optional<Collection<String>> scopes;

    @NonNull
    private final Optional<Long> expiry;

    @NonNull
    private final Optional<Long> notBefore;

    DefaultTokenIntrospectionResponse(boolean active, @NonNull Optional<String> subject, @NonNull Optional<Collection<String>> scopes, @NonNull Optional<Long> expiry, @NonNull Optional<Long> notBefore) {
        this.active = active;
        this.subject = subject;
        this.scopes = scopes;
        this.expiry = expiry;
        this.notBefore = notBefore;
    }

    public static DefaultTokenIntrospectionResponseBuilder newBuilder() {
        return new DefaultTokenIntrospectionResponseBuilder();
    }

    public static class DefaultTokenIntrospectionResponseBuilder {
        private boolean active;
        private String subject;
        private Collection<String> scopes;
        private Long expiry;
        private Long notBefore;

        DefaultTokenIntrospectionResponseBuilder() {
        }

        public DefaultTokenIntrospectionResponseBuilder active(boolean active) {
            this.active = active;
            return this;
        }

        public DefaultTokenIntrospectionResponseBuilder subject(@NonNull Optional<String> subject) {
            this.subject = subject.orElse(null);
            return this;
        }

        public DefaultTokenIntrospectionResponseBuilder subject(String subject) {
            this.subject = subject;
            return this;
        }

        public DefaultTokenIntrospectionResponseBuilder scopes(@NonNull Optional<Collection<String>> scopes) {
            this.scopes = scopes.orElse(null);
            return this;
        }

        public DefaultTokenIntrospectionResponseBuilder scopes(Collection<String> scopes) {
            this.scopes = scopes;
            return this;
        }

        public DefaultTokenIntrospectionResponseBuilder expiry(@NonNull Optional<Long> expiry) {
            this.expiry = expiry.orElse(null);
            return this;
        }

        public DefaultTokenIntrospectionResponseBuilder expiry(Long expiry) {
            this.expiry = expiry;
            return this;
        }

        public DefaultTokenIntrospectionResponseBuilder notBefore(@NonNull Optional<Long> notBefore) {
            this.notBefore = notBefore.orElse(null);
            return this;
        }

        public DefaultTokenIntrospectionResponseBuilder notBefore(Long notBefore) {
            this.notBefore = notBefore;
            return this;
        }

        public DefaultTokenIntrospectionResponse build() {
            return new DefaultTokenIntrospectionResponse(
                    active,
                    ofNullable(subject),
                    ofNullable(scopes),
                    ofNullable(expiry),
                    ofNullable(notBefore));
        }

        public String toString() {
            return "DefaultTokenIntrospectionResponse.DefaultTokenIntrospectionResponseBuilder(valid=" + this.active + ", subject=" + this.subject + ", scopes=" + this.scopes + ", expiry=" + this.expiry + ", notBefore=" + this.notBefore + ")";
        }
    }
}
