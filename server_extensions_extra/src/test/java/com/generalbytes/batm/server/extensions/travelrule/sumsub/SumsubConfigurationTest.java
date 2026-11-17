package com.generalbytes.batm.server.extensions.travelrule.sumsub;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SumsubConfigurationTest {

    @Test
    void testToString() {
        SumsubConfiguration configuration = new SumsubConfiguration();
        configuration.setWebhooksEnabled(true);
        configuration.setOriginatorWalletAddresses(Map.of("BTC", "bc1q_hot_wallet"));

        assertEquals("SumsubConfiguration(webhooksEnabled=true, originatorWalletAddresses={BTC=bc1q_hot_wallet})", configuration.toString());
    }

}