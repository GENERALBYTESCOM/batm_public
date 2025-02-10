package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.bitpandapro;

import com.generalbytes.batm.server.extensions.IRateSourceAdvanced;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static com.generalbytes.batm.common.currencies.CryptoCurrency.BTC;
import static com.generalbytes.batm.common.currencies.CryptoCurrency.DOGE;
import static com.generalbytes.batm.common.currencies.CryptoCurrency.ETH;
import static com.generalbytes.batm.common.currencies.CryptoCurrency.XRP;
import static com.generalbytes.batm.common.currencies.FiatCurrency.CHF;
import static com.generalbytes.batm.common.currencies.FiatCurrency.EUR;
import static com.generalbytes.batm.common.currencies.FiatCurrency.GBP;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Disabled // requires online resources - for manual run only
class BitpandaProRateSourceTest {

    private final IRateSourceAdvanced subject = BitpandaProExchange.asRateSource(EUR.getCode());

    @Test
    void shouldFetchCryptoCurrencies() {
        assertNotNull(subject.getCryptoCurrencies());
    }

    @Test
    void shouldFetchFiatCurrencies() {
        assertNotNull(subject.getFiatCurrencies());
    }

    @Test
    void shouldYieldConfiguredPreferredFiatCurrency() {
        assertEquals("EUR", subject.getPreferredFiatCurrency());
    }

    @Test
    void shouldFailOnInvalidInstrument() {
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
        // DOGE only supported with EUR
        assertNull(subject.getExchangeRateLast("DOGE", "CHF"));
    }

    @Test
    void shouldFetchLastExchangeRate() {
        // fetch fresh
        final BigDecimal fresh = subject.getExchangeRateLast(BTC.getCode(), EUR.getCode());
        assertNotNull(fresh);

        // second time is cached
        final BigDecimal cached = subject.getExchangeRateLast(BTC.getCode(), EUR.getCode());
        assertThat(cached).isEqualByComparingTo(fresh);
    }

    @Test
    void shouldCalculateReasonableBuyAndSellPricesForOneBitcoin() {
        final BigDecimal buy = subject.calculateBuyPrice(BTC.getCode(), EUR.getCode(), BigDecimal.ONE);
        assertNotNull(buy);
        assertTrue(buy.compareTo(BigDecimal.ZERO) > 0, "calculated non-positive buy price");

        final BigDecimal sell = subject.calculateSellPrice(BTC.getCode(), EUR.getCode(), BigDecimal.ONE);
        assertNotNull(sell);
        assertTrue(sell.compareTo(BigDecimal.ZERO) > 0, "calculated non-positive sell price");

        assertTrue(buy.compareTo(sell) >= 0, "buy price is smaller than sell");
    }

    @Test
    void shouldCalculateReasonableBuyAndSellRatesForBitcoin() {
        final BigDecimal buy = subject.getExchangeRateForBuy(BTC.getCode(), EUR.getCode());
        assertNotNull(buy);
        assertTrue(buy.compareTo(BigDecimal.ZERO) > 0, "calculated non-positive buy price");

        final BigDecimal sell = subject.getExchangeRateForSell(BTC.getCode(), EUR.getCode());
        assertNotNull(sell);
        assertTrue(sell.compareTo(BigDecimal.ZERO) > 0, "calculated non-positive sell price");

        assertTrue(buy.compareTo(sell) >= 0, "buy price is smaller than sell");
    }

    @Test
    void shouldSupportAllConfiguredPairs() {
        assertNotNull(subject.getExchangeRateLast(BTC.getCode(), EUR.getCode()), "BTC/EUR");
        assertNotNull(subject.getExchangeRateLast(ETH.getCode(), EUR.getCode()), "ETH/EUR");
        assertNotNull(subject.getExchangeRateLast(XRP.getCode(), EUR.getCode()), "XRP/EUR");
        assertNotNull(subject.getExchangeRateLast(BTC.getCode(), CHF.getCode()), "BTC/CHF");
        assertNotNull(subject.getExchangeRateLast(ETH.getCode(), CHF.getCode()), "ETH/CHF");
        assertNotNull(subject.getExchangeRateLast(XRP.getCode(), CHF.getCode()), "XRP/CHF");
        assertNotNull(subject.getExchangeRateLast(BTC.getCode(), GBP.getCode()), "BTC/GBP");
        assertNotNull(subject.getExchangeRateLast(DOGE.getCode(), EUR.getCode()), "DOGE/EUR");
    }
}
