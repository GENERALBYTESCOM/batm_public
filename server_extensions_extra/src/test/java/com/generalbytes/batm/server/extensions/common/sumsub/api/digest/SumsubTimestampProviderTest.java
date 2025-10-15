package com.generalbytes.batm.server.extensions.common.sumsub.api.digest;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertTrue;

class SumsubTimestampProviderTest {

    private final SumsubTimestampProvider provider = new SumsubTimestampProvider();

    @Test
    void testDigestParams() {
        String nowAsString = provider.digestParams(null);

        long now = Long.parseLong(nowAsString);
        long nowPlusSecond = Instant.now().plusSeconds(1).getEpochSecond();

        assertTrue(now < nowPlusSecond);
    }

}