package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.bitpandapro;

import static com.generalbytes.batm.common.currencies.CryptoCurrency.BTC;
import static com.generalbytes.batm.common.currencies.CryptoCurrency.ETH;
import static com.generalbytes.batm.common.currencies.CryptoCurrency.XRP;
import static com.generalbytes.batm.common.currencies.FiatCurrency.CHF;
import static com.generalbytes.batm.common.currencies.FiatCurrency.EUR;
import static com.generalbytes.batm.common.currencies.FiatCurrency.GBP;
import static com.generalbytes.batm.common.currencies.FiatCurrency.TRY;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;

import org.junit.Ignore;
import org.junit.Test;

import com.generalbytes.batm.server.extensions.IRateSourceAdvanced;

@Ignore // requires online resources - for manual run only
public class BitpandaProRateSourceTest {

    private final IRateSourceAdvanced subject = BitpandaProExchange.asRateSource(EUR.getCode());

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
    public void shouldFailOnInvalidInstrument() {
        // bogus crypto
        assertNull(subject.getExchangeRateLast("GAGA", "EUR"));
        // bogus fiat
        assertNull(subject.getExchangeRateLast("BTC", "GAGA"));
        // fiat <> fiat
        assertNull(subject.getExchangeRateLast("EUR", "CHF"));
        // crypto <> crypto
        assertNull(subject.getExchangeRateLast("BTC", "ETH"));
        // GBP only supports BTC
        assertNull(subject.getExchangeRateLast("ETH", "GBP"));
        // TRY only supports BTC and ETH
        assertNull(subject.getExchangeRateLast("XRP", "TRY"));
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

    @Test
    public void shouldSupportAllConfiguredPairs() {
        assertNotNull("BTC/EUR", subject.getExchangeRateLast(BTC.getCode(), EUR.getCode()));
        assertNotNull("ETH/EUR", subject.getExchangeRateLast(ETH.getCode(), EUR.getCode()));
        assertNotNull("XRP/EUR", subject.getExchangeRateLast(XRP.getCode(), EUR.getCode()));
        assertNotNull("BTC/CHF", subject.getExchangeRateLast(BTC.getCode(), CHF.getCode()));
        assertNotNull("ETH/CHF", subject.getExchangeRateLast(ETH.getCode(), CHF.getCode()));
        assertNotNull("XRP/CHF", subject.getExchangeRateLast(XRP.getCode(), CHF.getCode()));
        assertNotNull("BTC/GBP", subject.getExchangeRateLast(BTC.getCode(), GBP.getCode()));
        assertNotNull("BTC/TRY", subject.getExchangeRateLast(BTC.getCode(), TRY.getCode()));
        assertNotNull("ETH/TRY", subject.getExchangeRateLast(ETH.getCode(), TRY.getCode()));
    }
}
