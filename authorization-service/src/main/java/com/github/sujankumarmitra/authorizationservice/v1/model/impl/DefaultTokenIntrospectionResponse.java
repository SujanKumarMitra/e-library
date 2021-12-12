package com.github.sujankumarmitra.authorizationservice.v1.model.impl;

import com.github.sujankumarmitra.authorizationservice.v1.model.TokenIntrospectionResponse;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

import java.util.Collection;
import java.util.Optional;

import static java.util.Optional.ofNullable;

/**
 * @author skmitra
 * @since Nov 17/11/21, 2021
 */

@Getter
@ToString
public class DefaultTokenIntrospectionResponse extends TokenIntrospectionResponse {
    private boolean active;

    @NonNull
    private final Optional<String> subject;

    @NonNull
    private final Optional<Collection<String>> scopes;

    @NonNull
    private final Optional<Long> expiry;


    DefaultTokenIntrospectionResponse(boolean active, @NonNull Optional<String> subject, @NonNull Optional<Collection<String>> scopes, @NonNull Optional<Long> expiry) {
        this.active = active;
        this.subject = subject;
        this.scopes = scopes;
        this.expiry = expiry;
    }

    public static DefaultTokenIntrospectionResponseBuilder newBuilder() {
        return new DefaultTokenIntrospectionResponseBuilder();
    }

    public static class DefaultTokenIntrospectionResponseBuilder {
        private boolean active;
        private String subject;
        private Collection<String> scopes;
        private Long expiry;

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

        public DefaultTokenIntrospectionResponse build() {
            return new DefaultTokenIntrospectionResponse(
                    active,
                    ofNullable(subject),
                    ofNullable(scopes),
                    ofNullable(expiry));
        }

        public String toString() {
            return "DefaultTokenIntrospectionResponse.DefaultTokenIntrospectionResponseBuilder(valid=" + this.active + ", subject=" + this.subject + ", scopes=" + this.scopes + ", expiry=" + this.expiry + ")";
        }
    }
}
