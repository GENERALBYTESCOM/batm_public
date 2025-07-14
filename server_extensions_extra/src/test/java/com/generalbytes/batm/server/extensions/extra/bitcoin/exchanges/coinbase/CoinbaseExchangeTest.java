package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.coinbase;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CoinbaseExchangeTest {

    @Test
    void testGetPreferredFiatCurrency() {
        CoinbaseExchange coinbaseExchange = new CoinbaseExchange(null, null, "CZK", null);

        String preferredFiatCurrency = coinbaseExchange.getPreferredFiatCurrency();

        assertEquals("CZK", preferredFiatCurrency);
    }

    @Test
    void testGetPreferredFiatCurrency_default() {
        CoinbaseExchange coinbaseExchange = new CoinbaseExchange(null, null, null, null);

        String preferredFiatCurrency = coinbaseExchange.getPreferredFiatCurrency();

        assertEquals("USD", preferredFiatCurrency);
    }

}