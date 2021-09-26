package com.github.sujankumarmitra.assetservice.v1.controller;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * @author skmitra
 * @since Sep 26/09/21, 2021
 */
@Slf4j
class UriTest {


    @Test
    void testAcceptedUri() {
        URI uri = assertDoesNotThrow(() -> URI.create("/somepath"));
        log.info("Created URI: '{}'", uri);
    }
}
