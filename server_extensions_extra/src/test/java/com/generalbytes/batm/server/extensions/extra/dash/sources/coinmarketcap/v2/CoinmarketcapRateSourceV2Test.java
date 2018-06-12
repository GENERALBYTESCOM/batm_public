package com.generalbytes.batm.server.extensions.extra.dash.sources.coinmarketcap.v2;

import org.junit.Assert;
import org.junit.BeforeClass;
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
        CoinmarketcapRateSourceV2 rateSource = new CoinmarketcapRateSourceV2(null);

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

    /**
     * Unit test method getExchangeRateLastTest() create an object of type CoinmarketcapRateSourceV2
     * with parameter USD
     */
    @Test
    public void getExchangeRateUSDTest() {
        CoinmarketcapRateSourceV2 rateSource = new CoinmarketcapRateSourceV2("USD");

        final BigDecimal priceUSD = rateSource.getExchangeRateLast("BTC", "USD");
        Assert.assertNotNull(priceUSD);

        final BigDecimal priceEUR = rateSource.getExchangeRateLast("BTC", "EUR");
        Assert.assertNotNull(priceEUR);

        Assert.assertNotSame(priceUSD, priceEUR);

        final BigDecimal priceEthEur = rateSource.getExchangeRateLast("ETH", "EUR");
        Assert.assertNotNull(priceEthEur);

        final BigDecimal priceEthUsd = rateSource.getExchangeRateLast("ETH", "USD");
        Assert.assertNotNull(priceEthUsd);

        Assert.assertNotSame(priceEthUsd, priceEthEur);
    }

    /**
     * Unit test method getExchangeRateLastTest() create an object of type CoinmarketcapRateSourceV2
     * with parameter EUR
     */
    @Test
    public void getExchangeRateEURTest() {
        CoinmarketcapRateSourceV2 rateSource = new CoinmarketcapRateSourceV2("EUR");

        final BigDecimal priceUSD = rateSource.getExchangeRateLast("BTC", "USD");
        Assert.assertNotNull(priceUSD);

        final BigDecimal priceEUR = rateSource.getExchangeRateLast("BTC", "EUR");
        Assert.assertNotNull(priceEUR);

        Assert.assertNotSame(priceUSD, priceEUR);

        final BigDecimal priceEthEur = rateSource.getExchangeRateLast("ETH", "EUR");
        Assert.assertNotNull(priceEthEur);

        final BigDecimal priceEthUsd = rateSource.getExchangeRateLast("ETH", "USD");
        Assert.assertNotNull(priceEthUsd);

        Assert.assertNotSame(priceEthUsd, priceEthEur);
    }

}
