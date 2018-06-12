package com.generalbytes.batm.server.extensions.extra.dash.sources.coinmarketcap.v2;

import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;

/**
 * Class CoinmarketcapRateSourceV2Test tests method getExchangeRateLast() of class CoinmarketcapRateSourceV2
 */
public class CoinmarketcapRateSourceV2Test {

    /**
     * Unit test method getExchangeRateLastTest() create an object of type CoinmarketcapRateSourceV2
     * with parameter null
     */
    @Test
    public void getExchangeRateLastTest() {
        CoinmarketcapRateSourceV2 rateSource = new CoinmarketcapRateSourceV2();

        final BigDecimal priceUSD = rateSource.getExchangeRateLast("BTC", "USD");
        Assert.assertNotNull(priceUSD);
        System.out.println("price for btc = " + priceUSD.doubleValue() + " usd");

        final BigDecimal priceEUR = rateSource.getExchangeRateLast("BTC", "EUR");
        Assert.assertNotNull(priceEUR);
        System.out.println("price for btc = " + priceEUR.doubleValue() + " eur");

        Assert.assertNotSame(priceUSD, priceEUR);

        final BigDecimal priceEthEur = rateSource.getExchangeRateLast("ETH", "EUR");
        Assert.assertNotNull(priceEthEur);
        System.out.println("price for eth = " + priceEthEur.doubleValue() + " eur");

        final BigDecimal priceEthUsd = rateSource.getExchangeRateLast("ETH", "USD");
        Assert.assertNotNull(priceEthUsd);
        System.out.println("price for eth = " + priceEthUsd.doubleValue() + " usd");

        Assert.assertNotSame(priceEthUsd, priceEthEur);
    }

}
