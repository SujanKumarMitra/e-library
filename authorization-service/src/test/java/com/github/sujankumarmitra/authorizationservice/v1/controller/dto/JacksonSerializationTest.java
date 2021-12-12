package com.github.sujankumarmitra.authorizationservice.v1.controller.dto;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.github.sujankumarmitra.authorizationservice.v1.model.impl.DefaultTokenIntrospectionResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author skmitra
 * @since Nov 17/11/21, 2021
 */
class JacksonSerializationTest {

    private ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mapper.registerModule(new Jdk8Module());
    }

    @Test
    void givenInvalidTokenIntrospectionResponse_whenEncode_shouldOnlyIncludeValidField() throws Exception {

        DefaultTokenIntrospectionResponse invalidTokenResponse = DefaultTokenIntrospectionResponse
                .newBuilder()
                .active(false)
                .build();

        JacksonTokenIntrospectionResponse jacksonResponse = new JacksonTokenIntrospectionResponse(invalidTokenResponse);

        String jsonResponse = mapper.writeValueAsString(jacksonResponse);

        assertThat(jsonResponse).isEqualTo("{\"active\":false}");

    }


    /**
     * <a href="https://www.rfc-editor.org/rfc/rfc7662#section-2.2">Introspection Response</a>
     */
    @Test
    void givenValidTokenIntrospectionResult_whenDecoded_shouldHaveFieldNamesAsDefinedInRFC7662() throws Exception {


        DefaultTokenIntrospectionResponse validResponse = DefaultTokenIntrospectionResponse
                .newBuilder()
                .active(true)
                .scopes(List.of("scope1"))
                .expiry(1637154940522L)
                .subject("subject")
                .build();

        JacksonTokenIntrospectionResponse jacksonResponse = new JacksonTokenIntrospectionResponse(validResponse);

        String jsonResponse = mapper.writeValueAsString(jacksonResponse);

        System.out.println(jsonResponse);

        JsonNode root = mapper.reader().readTree(jsonResponse);

        assertThat(root.get("active").asBoolean()).isTrue();
        assertThat(root.get("nbf").asLong()).isEqualTo(1637154940522L);
        assertThat(root.get("exp").asLong()).isEqualTo(1637154940522L);
        assertThat(root.get("sub").asText()).isEqualTo("subject");

        JsonNode scopesNode = root.get("scopes");

        assertThat(scopesNode.isArray()).isTrue();
        assertThat(scopesNode.get(0).asText()).isEqualTo("scope1");


    }


}
