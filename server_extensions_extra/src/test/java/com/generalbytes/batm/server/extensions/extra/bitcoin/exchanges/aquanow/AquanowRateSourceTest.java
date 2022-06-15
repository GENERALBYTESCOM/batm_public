package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.aquanow;

import com.generalbytes.batm.server.extensions.IRateSourceAdvanced;
import com.generalbytes.batm.server.extensions.extra.bitcoin.BitcoinExtension;
import org.junit.Ignore;
import org.junit.Test;

import java.math.BigDecimal;

import static com.generalbytes.batm.common.currencies.CryptoCurrency.*;
import static com.generalbytes.batm.common.currencies.FiatCurrency.*;
import static org.junit.Assert.*;

@Ignore // requires online resources - for manual run only
public class AquanowRateSourceTest {

    BitcoinExtension bitcoinExtension = new BitcoinExtension();

    private IRateSourceAdvanced subject = (IRateSourceAdvanced) bitcoinExtension.createRateSource("aquanow");


    @Test
    public void shouldFetchCryptoCurrencies() {
        System.out.println(subject.getCryptoCurrencies());
        assertNotNull(subject.getCryptoCurrencies());
    }

    @Test
    public void shouldFetchFiatCurrencies() {
        System.out.println(subject.getFiatCurrencies());
        assertNotNull(subject.getFiatCurrencies());
    }

    @Test
    public void shouldSupportAllConfiguredPairs() {
        assertNotNull("BTC/CAD", subject.getExchangeRateLast(BTC.getCode(), CAD.getCode()));
        assertNotNull("ETH/CAD", subject.getExchangeRateLast(ETH.getCode(), CAD.getCode()));
    }

    @Test
    public void shouldFetchLastExchangeRate() {
        // fetch fresh
        final BigDecimal fresh = subject.getExchangeRateLast(BTC.getCode(), CAD.getCode());
        System.out.println("rate last: "+fresh);
        assertNotNull(fresh);

        // second time is cached
        final BigDecimal cached = subject.getExchangeRateLast(BTC.getCode(), CAD.getCode());
        System.out.println(cached);
    }

    @Test
    public void shouldCalculateReasonableBuyAndSellPricesForOneBitcoin() {
        final BigDecimal buy = subject.calculateBuyPrice(BTC.getCode(), CAD.getCode(), new BigDecimal("0.1"));
        System.out.println("buy price:"+buy);
        assertNotNull(buy);
        assertTrue("calculated non-positive buy price", buy.compareTo(BigDecimal.ZERO) > 0);

        final BigDecimal sell = subject.calculateSellPrice(BTC.getCode(), CAD.getCode(), new BigDecimal("0.1"));
        System.out.println("sell price:"+sell);
        assertNotNull(sell);
        assertTrue("calculated non-positive sell price", sell.compareTo(BigDecimal.ZERO) > 0);

        assertTrue("buy price is smaller than sell", buy.compareTo(sell) >= 0);
    }

    @Test
    public void shouldCalculateReasonableBuyAndSellRatesForBitcoin() {
        final BigDecimal buy = subject.getExchangeRateForBuy(BTC.getCode(), CAD.getCode());
        System.out.println("buy price:"+buy);
        assertNotNull(buy);
        assertTrue("calculated non-positive buy price", buy.compareTo(BigDecimal.ZERO) > 0);

        final BigDecimal sell = subject.getExchangeRateForSell(BTC.getCode(), CAD.getCode());
        System.out.println("sell price:"+sell);
        assertNotNull(sell);
        assertTrue("calculated non-positive sell price", sell.compareTo(BigDecimal.ZERO) > 0);

        assertTrue("buy price is smaller than sell", buy.compareTo(sell) >= 0);
    }

}
