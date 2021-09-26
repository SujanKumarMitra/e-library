package com.github.sujankumarmitra.assetservice.v1.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Instant;

/**
 * @author skmitra
 * @since Sep 26/09/21, 2021
 */
class TimestampTest {

    @Test
    void testEpochMillisecondsFromSystemClassAndInstantClassAreSame() {
        long epochSecond = Instant.now().toEpochMilli();
        long timeMillis = System.currentTimeMillis();

        Assertions.assertEquals(epochSecond, timeMillis);
    }
}
