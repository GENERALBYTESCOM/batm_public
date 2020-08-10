package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.bitpandapro;

import static com.generalbytes.batm.common.currencies.CryptoCurrency.BTC;
import static com.generalbytes.batm.common.currencies.FiatCurrency.EUR;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.net.URI;

import org.junit.Ignore;
import org.junit.Test;

import com.generalbytes.batm.server.extensions.IRateSourceAdvanced;

@Ignore // requires online resources - for manual run only
public class BitpandaProRateSourceTest {
    /** exchange sandbox */
    private static final URI API = URI.create("https://api.exchange.waskurzes.com");

    private final IRateSourceAdvanced subject = new BitpandaProExchange(API, null, EUR.getCode());

    @Test
    public void shouldFetchCryptoCurrencies() {
        assertNotNull(subject.getCryptoCurrencies());
    }

    @Test
    public void shouldFetchFiatCurrencies() {
        assertNotNull(subject.getFiatCurrencies());
    }

    @Test
    public void shouldYieldConfiguredPreferredFiatCurrency() {
        assertEquals("EUR", subject.getPreferredFiatCurrency());
    }

    @Test
    public void shouldFetchLastExchangeRate() {
        // fetch fresh
        final BigDecimal fresh = subject.getExchangeRateLast(BTC.getCode(), EUR.getCode());
        assertNotNull(fresh);

        // second time is cached
        final BigDecimal cached = subject.getExchangeRateLast(BTC.getCode(), EUR.getCode());
        assertEquals(fresh, cached);
    }

    @Test
    public void shouldCalculateReasonableBuyAndSellPricesForOneBitcoin() {
        final BigDecimal buy = subject.calculateBuyPrice(BTC.getCode(), EUR.getCode(), BigDecimal.ONE);
        assertNotNull(buy);
        assertTrue("calculated non-positive buy price", buy.compareTo(BigDecimal.ZERO) > 0);

        final BigDecimal sell = subject.calculateSellPrice(BTC.getCode(), EUR.getCode(), BigDecimal.ONE);
        assertNotNull(sell);
        assertTrue("calculated non-positive sell price", sell.compareTo(BigDecimal.ZERO) > 0);

        assertTrue("buy price is smaller than sell", buy.compareTo(sell) >= 0);
    }

    @Test
    public void shouldCalculateReasonableBuyAndSellRatesForBitcoin() {
        final BigDecimal buy = subject.getExchangeRateForBuy(BTC.getCode(), EUR.getCode());
        assertNotNull(buy);
        assertTrue("calculated non-positive buy price", buy.compareTo(BigDecimal.ZERO) > 0);

        final BigDecimal sell = subject.getExchangeRateForSell(BTC.getCode(), EUR.getCode());
        assertNotNull(sell);
        assertTrue("calculated non-positive sell price", sell.compareTo(BigDecimal.ZERO) > 0);

        assertTrue("buy price is smaller than sell", buy.compareTo(sell) >= 0);
    }
}
