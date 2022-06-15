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

import com.generalbytes.batm.server.extensions.IExchange;
import com.generalbytes.batm.server.extensions.IExchangeAdvanced;
import com.generalbytes.batm.server.extensions.ITask;
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
        String purchaseId = exchange.purchaseCoins(BigDecimal.TEN, cryptoCurrency,"USD", "test");
        System.out.println(purchaseId);
        Assert.assertNotNull(purchaseId);
    }

    @Test
    public void testPurchaseAdvanced() {
        if (exchange instanceof IExchangeAdvanced) {
            ITask tt = ((IExchangeAdvanced) exchange).createPurchaseCoinsTask(new BigDecimal("0.00000001"), cryptoCurrency, exchange.getPreferredFiatCurrency(), "test");
            tt.onCreate();
            for (int i = 0; i < 10 && !tt.isFinished(); i++) {
                tt.onDoStep();
            }
            String purchaseId = tt.getResult() == null ? null : tt.getResult().toString();
            System.out.println(purchaseId);
            Assert.assertNotNull(purchaseId);
        }
    }
    @Test
    public void testSellAdvanced() {
        if (exchange instanceof IExchangeAdvanced) {
            ITask tt = ((IExchangeAdvanced) exchange).createSellCoinsTask(new BigDecimal("0.00000001"), cryptoCurrency, exchange.getPreferredFiatCurrency(), "test");
            tt.onCreate();
            for (int i = 0; i < 10 && !tt.isFinished(); i++) {
                tt.onDoStep();
            }
            String purchaseId = tt.getResult() == null ? null : tt.getResult().toString();
            System.out.println(purchaseId);
            Assert.assertNotNull(purchaseId);
        }
    }
    @Test
    public void testGetCryptoBalance() {
        BigDecimal cryptoBalance = exchange.getCryptoBalance(cryptoCurrency);
        System.out.println(cryptoBalance);
        Assert.assertNotNull(cryptoBalance);
    }
    @Test
    public void testGetFiatBalance() {
        BigDecimal fiatBalance = exchange.getFiatBalance(exchange.getPreferredFiatCurrency());
        System.out.println(fiatBalance);
        Assert.assertNotNull(fiatBalance);
    }

    @Test
    public void testGetDepositAddress() {
        String depositAddress = exchange.getDepositAddress(cryptoCurrency);
        System.out.println(depositAddress);
        Assert.assertNotNull(depositAddress);
    }

    @Test
    public void testSendCoins() {
        String res = exchange.sendCoins(exchange.getDepositAddress(cryptoCurrency), exchange.getCryptoBalance(cryptoCurrency), cryptoCurrency, "test");
        System.out.println(res);
        Assert.assertNotNull(res);
    }

}