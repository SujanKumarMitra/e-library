package com.github.sujankumarmitra.authorizationservice.v1.service;

import org.junit.jupiter.api.Test;

import java.util.Objects;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author skmitra
 * @since Nov 17/11/21, 2021
 */
class NullElementStreamTest {


    @Test
    void givenArrayWithNulls_whenConvertedToStream_streamShouldHaveNull() {

        Object[] arr = {null, null, null};
        boolean result = Stream.of(arr).allMatch(Objects::isNull);
        assertThat(result).isTrue();
    }

}
