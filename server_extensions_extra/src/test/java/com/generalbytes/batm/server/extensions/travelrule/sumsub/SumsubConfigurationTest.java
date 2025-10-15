package com.generalbytes.batm.server.extensions.travelrule.sumsub;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SumsubConfigurationTest {

    @Test
    void testToString() {
        SumsubConfiguration configuration = new SumsubConfiguration();
        configuration.setWebhooksEnabled(true);

        assertEquals("SumsubConfiguration(webhooksEnabled=true)", configuration.toString());
    }

}