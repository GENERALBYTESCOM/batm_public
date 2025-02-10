package com.generalbytes.batm.server.extensions.extra.dash.sources.coinmarketcap;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Class CoinmarketcapRateSourceV2Test tests method getExchangeRateLast() of class CoinmarketcapRateSourceV2
 */
@Disabled("Requires external systems connectivity")
class CoinmarketcapRateSourceTest {
    private static final Logger log = LoggerFactory.getLogger("batm.master.extensions.CoinmarketcapRateSourceTest");

    @Test
    void nullApi() {
        assertNull(new CoinmarketcapRateSource(null, "USD").getExchangeRateLast("BTC", "CZK"));
    }

    /**
     * Unit test method getExchangeRateLastTest() create an object of type CoinmarketcapRateSourceV2
     * with parameter null
     */
    @Test
    void getExchangeRateLastTest() {
        CoinmarketcapRateSource rateSource = new CoinmarketcapRateSource("ce83e9de-db96-4cdd-871d-cf9eb07a8381", "USD");

        final BigDecimal priceUSD = rateSource.getExchangeRateLast("BTC", "USD");
        assertNotNull(priceUSD);
        log.info("price for btc = " + priceUSD.doubleValue() + " usd");

        final BigDecimal priceEUR = rateSource.getExchangeRateLast("BTC", "EUR");
        assertNotNull(priceEUR);
        log.info("price for btc = " + priceEUR.doubleValue() + " eur");

        assertNotSame(priceUSD, priceEUR);

        final BigDecimal priceEthEur = rateSource.getExchangeRateLast("ETH", "EUR");
        assertNotNull(priceEthEur);
        log.info("price for eth = " + priceEthEur.doubleValue() + " eur");

        final BigDecimal priceEthUsd = rateSource.getExchangeRateLast("ETH", "USD");
        assertNotNull(priceEthUsd);
        log.info("price for eth = " + priceEthUsd.doubleValue() + " usd");

        assertNotSame(priceEthUsd, priceEthEur);
    }

}
