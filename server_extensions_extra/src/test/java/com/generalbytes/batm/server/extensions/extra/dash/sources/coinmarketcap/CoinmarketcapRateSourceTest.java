package com.generalbytes.batm.server.extensions.extra.dash.sources.coinmarketcap;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

/**
 * Class CoinmarketcapRateSourceV2Test tests method getExchangeRateLast() of class CoinmarketcapRateSourceV2
 */
public class CoinmarketcapRateSourceTest {
    private static final Logger log = LoggerFactory.getLogger("batm.master.extensions.CoinmarketcapRateSourceTest");

    /**
     * Unit test method getExchangeRateLastTest() create an object of type CoinmarketcapRateSourceV2
     * with parameter null
     */
    @Test
    public void getExchangeRateLastTest() {
        CoinmarketcapRateSource rateSource = new CoinmarketcapRateSource("ba025ccf-579b-40e4-be05-cbcebd83c476", "USD");

        final BigDecimal priceUSD = rateSource.getExchangeRateLast("BTC", "USD");
        Assert.assertNotNull(priceUSD);
        log.info("price for btc = " + priceUSD.doubleValue() + " usd");

        final BigDecimal priceEUR = rateSource.getExchangeRateLast("BTC", "EUR");
        Assert.assertNotNull(priceEUR);
        log.info("price for btc = " + priceEUR.doubleValue() + " eur");

        Assert.assertNotSame(priceUSD, priceEUR);

        final BigDecimal priceEthEur = rateSource.getExchangeRateLast("ETH", "EUR");
        Assert.assertNotNull(priceEthEur);
        log.info("price for eth = " + priceEthEur.doubleValue() + " eur");

        final BigDecimal priceEthUsd = rateSource.getExchangeRateLast("ETH", "USD");
        Assert.assertNotNull(priceEthUsd);
        log.info("price for eth = " + priceEthUsd.doubleValue() + " usd");

        Assert.assertNotSame(priceEthUsd, priceEthEur);
    }

}
