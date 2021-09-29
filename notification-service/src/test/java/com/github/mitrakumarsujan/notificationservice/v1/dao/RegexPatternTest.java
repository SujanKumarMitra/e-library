package com.github.mitrakumarsujan.notificationservice.v1.dao;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.regex.Pattern;

/**
 * @author skmitra
 * @since Sep 29/09/21, 2021
 */
class RegexPatternTest {

    @Test
    void testPattern() {
        Pattern pattern = Pattern.compile("^consumerId");

        Assertions.assertTrue(pattern.matcher("consumerId#103232").find());

    }
}
