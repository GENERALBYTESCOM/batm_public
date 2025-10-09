package com.generalbytes.batm.server.extensions.extra.liquidbitcoin;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class LiquidBitcoinDefinitionTest {

    @Test
    void testDefinition() {
        LiquidBitcoinDefinition definition = new LiquidBitcoinDefinition();

        assertEquals(CryptoCurrency.L_BTC.getCode(), definition.getSymbol());
        assertEquals(CryptoCurrency.BTC.getCode(), definition.getRateSourceSymbol());
        assertEquals("Liquid Network Bitcoin", definition.getName());
        assertEquals("liquidnetwork", definition.getProtocol());
        assertEquals("https://liquid.net/", definition.getAuthorWebsiteURL());
        assertNull(definition.getPaymentSupport());
        // uncomment assertion below when SELL is supported
        //assertInstanceOf(LiquidBitcoinPaymentSupport.class, definition.getPaymentSupport());
    }
}