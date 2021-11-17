package com.github.sujankumarmitra.authorizationservice.v1.service.impl;

import com.github.sujankumarmitra.authorizationservice.v1.model.TokenIntrospectionRequest;
import com.github.sujankumarmitra.authorizationservice.v1.model.TokenIntrospectionResponse;
import com.github.sujankumarmitra.authorizationservice.v1.model.impl.DefaultTokenIntrospectionRequest;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author skmitra
 * @since Nov 17/11/21, 2021
 */
class JWTTokenIntrospectorTest {


    private JWTTokenIntrospector introspector = new JWTTokenIntrospector();

    private boolean isInactiveToken(TokenIntrospectionResponse res) {
        return !res.isActive();
    }

    @Test
    void givenValidToken_whenDecoded_shouldDecode() {

        String validJwtToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9" +
                "." +
                "eyJzdWIiOiJzdWJqZWN0Iiwic2NvcGVzIjpbInNjb3BlMSIsInNjb3BlMiJdLCJuYmYiOjE2MzcxNTQ1NDA1MjIsImV4cCI6MTYzNzE1NDk0MDUyMn0" +
                "." +
                "zRjaQGgEpFIn_TmYjhwKhuwwtAB5ttbr-XBX_AegW-o";

        TokenIntrospectionRequest request = new DefaultTokenIntrospectionRequest(validJwtToken);

        introspector.introspect(request)
                .as(StepVerifier::create)
                .expectSubscription()
                .consumeNextWith(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.isActive()).isTrue();
                    assertThat(response.getSubject()).contains("subject");
                    assertThat(response.getScopes()).contains(List.of("scope1", "scope2"));
                    assertThat(response.getNotBefore()).contains(1637154540522L);
                    assertThat(response.getExpiry()).contains(1637154940522L);
                })
                .verifyComplete();

    }


    @Test
    void givenMalformedJWTToken_whenDecoded_shouldReturnInactiveResponse() {
        String malformedToken = "malformed";
        TokenIntrospectionRequest request = new DefaultTokenIntrospectionRequest(malformedToken);

        introspector.introspect(request)
                .as(StepVerifier::create)
                .expectSubscription()
                .expectNextMatches(this::isInactiveToken)
                .verifyComplete();

    }

    @Test
    void givenValidJWTTokenButWithoutRequiredClaims_whenDecoded_shouldReturnInvalidResponse() {

        String withoutSubjectJwt = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9" +
                "." +
                "eyJzY29wZXMiOlsic2NvcGUxIiwic2NvcGUyIl0sIm5iZiI6MTYzNzE1NDU0MDUyMiwiZXhwIjoxNjM3MTU0OTQwNTIyfQ" +
                "." +
                "pmqFZfCBLl1sZD4YbeBBBNlC5-3xXvYR5PDnHCfjDus";

        TokenIntrospectionRequest request = new DefaultTokenIntrospectionRequest(withoutSubjectJwt);

        introspector.introspect(request)
                .as(StepVerifier::create)
                .expectSubscription()
                .expectNextMatches(this::isInactiveToken)
                .verifyComplete();


    }
}