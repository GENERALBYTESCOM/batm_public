/*************************************************************************************
 * Copyright (C) 2014-2020 GENERAL BYTES s.r.o. All rights reserved.
 *
 * This software may be distributed and modified under the terms of the GNU
 * General Public License version 2 (GPL2) as published by the Free Software
 * Foundation and appearing in the file GPL2.TXT included in the packaging of
 * this file. Please note that GPL2 Section 2[b] requires that all works based
 * on this software must also be made publicly available under the terms of
 * the GPL2 ("Copyleft").
 *
 * Contact information
 * -------------------
 *
 * GENERAL BYTES s.r.o.
 * Web      :  http://www.generalbytes.com
 *
 ************************************************************************************/
package com.generalbytes.batm.server.extensions.extra;

import com.generalbytes.batm.server.extensions.IRateSourceAdvanced;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertNotNull;

// All tests @Ignore'd here because they depend on external resources
// To be run manually, not as a part of the build
@Disabled
class RateSourceTest {
    private final String cryptoCurrency;
    private final IRateSourceAdvanced rateSource;

    public static Collection getTestData() {
        return Arrays.asList(new Object[][]{
//            {"LTC", new BinanceComExchange("USD")},
        });
    }

    // @Parameterized.Parameters annotated method results are passed here
    public RateSourceTest(String cryptoCurrency, IRateSourceAdvanced rateSource) {
        this.cryptoCurrency = cryptoCurrency;
        this.rateSource = rateSource;
    }

    @Test
    void testExchangeRateLast() {
        BigDecimal rate = rateSource.getExchangeRateLast(cryptoCurrency, rateSource.getPreferredFiatCurrency());
        System.out.println(rate);
        assertNotNull(rate);
    }

    @Test
    void testExchangeRateForSell() {
        BigDecimal rate = rateSource.getExchangeRateForSell(cryptoCurrency, rateSource.getPreferredFiatCurrency());
        System.out.println(rate);
        assertNotNull(rate);
    }

    @Test
    void testExchangeRateForBuy() {
        BigDecimal rate = rateSource.getExchangeRateForBuy(cryptoCurrency, rateSource.getPreferredFiatCurrency());
        System.out.println(rate);
        assertNotNull(rate);
    }

    @Test
    void testcalculateBuyPrice() {
        BigDecimal cryptoAmount = new BigDecimal("0.5");
        BigDecimal rate = rateSource.calculateBuyPrice(cryptoCurrency, rateSource.getPreferredFiatCurrency(), cryptoAmount).divide(cryptoAmount, RoundingMode.FLOOR);
        System.out.println(rate);
        assertNotNull(rate);
    }

    @Test
    void testcalculateSellPrice() {
        BigDecimal cryptoAmount = new BigDecimal("0.5");
        BigDecimal rate = rateSource.calculateSellPrice(cryptoCurrency, rateSource.getPreferredFiatCurrency(), cryptoAmount).divide(cryptoAmount, RoundingMode.FLOOR);
        System.out.println(rate);
        assertNotNull(rate);
    }

}