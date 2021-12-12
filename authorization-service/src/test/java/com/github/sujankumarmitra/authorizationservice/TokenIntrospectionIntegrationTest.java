package com.github.sujankumarmitra.authorizationservice;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.sujankumarmitra.authorizationservice.v1.model.TokenIntrospectionResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@SpringBootTest(webEnvironment = RANDOM_PORT)
class TokenIntrospectionIntegrationTest {

    @Autowired
    private WebTestClient client = null;

    @Test
    void activeJWTIntrospectionTest() {
        TokenIntrospectionResponse response = client.post()
                .uri("/introspect")
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData("token", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwic2NvcGVzIjpbInNjb3BlMSIsInNjb3BlMiJdLCJuYmYiOjE2MzcxNTQ1NDA1MjIsImV4cCI6MTYzNzE1NDk0MDUyMn0.0lMo0guMBkNA8E9MJRwR0-qiQWdkRyDAm3Zy3l674CM"))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(TokenIntrospectionResponseImpl.class)
                .returnResult()
                .getResponseBody();

        assertThat(response.isActive()).isTrue();
        assertThat(response.getSubject()).contains("1234567890");
        assertThat(response.getExpiry()).contains(1637154940522L);
        assertThat(response.getScopes()).contains(List.of("scope1", "scope2"));

    }


    @Test
    void malformedTokenIntrospectionTest() {
        TokenIntrospectionResponse response = client.post()
                .uri("/introspect")
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData("token", "malformed.."))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(TokenIntrospectionResponseImpl.class)
                .returnResult()
                .getResponseBody();

        assertThat(response.isActive()).isFalse();
    }


    @Test
    void missingTokenIntrospectionTest() {
        HttpStatus badRequestStatus = client.post()
                .uri("/introspect")
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_FORM_URLENCODED)
                .exchange()
                .returnResult(TokenIntrospectionResponseImpl.class)
                .getStatus();

        assertThat(badRequestStatus).isEqualTo(BAD_REQUEST);

    }


    @Getter
    @Setter
    @ToString
    static class TokenIntrospectionResponseImpl extends TokenIntrospectionResponse {
        boolean active;
        @JsonProperty("sub")
        Optional<String> subject;
        Optional<Collection<String>> scopes;
        @JsonProperty("exp")
        Optional<Long> expiry;
    }
}
