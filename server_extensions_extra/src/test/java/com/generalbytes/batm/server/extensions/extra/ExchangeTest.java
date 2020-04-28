/*************************************************************************************
 * Copyright (C) 2014-2019 GENERAL BYTES s.r.o. All rights reserved.
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

import com.generalbytes.batm.server.extensions.IExchange;
import com.generalbytes.batm.server.extensions.IExchangeAdvanced;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;

// All tests @Ignore'd here because they depend on external resources
// To be run manually, not as a part of the build
@Ignore
@RunWith(Parameterized.class)
public class ExchangeTest {
    private final String cryptoCurrency;
    private final IExchange exchange;

    @Parameterized.Parameters
    public static Collection getTestData() {
        return Arrays.asList(new Object[][]{
//            {"LTC", new BinanceComExchange("", "", "EUR")},
//            {"BTC", new BinanceComExchange("", "", "EUR")},
        });
    }

    // @Parameterized.Parameters annotated method results are passed here
    public ExchangeTest(String cryptoCurrency, IExchange exchange) {
        this.cryptoCurrency = cryptoCurrency;
        this.exchange = exchange;
    }


    @Test
    public void testPurchase() {
        if (exchange instanceof IExchangeAdvanced) {
            ((IExchangeAdvanced) exchange).createPurchaseCoinsTask(BigDecimal.TEN, cryptoCurrency, "USD", "test");
        }
    }

    @Test
    public void testGetCryptoBalance() {
        BigDecimal cryptoBalance = exchange.getCryptoBalance(cryptoCurrency);
        System.out.println(cryptoBalance);
        Assert.assertNotNull(cryptoBalance);
    }

    @Test
    public void testSendCoins() {
        String res = exchange.sendCoins(exchange.getDepositAddress(cryptoCurrency), exchange.getCryptoBalance(cryptoCurrency), cryptoCurrency, "test");
        System.out.println(res);
        Assert.assertNotNull(res);
    }

}