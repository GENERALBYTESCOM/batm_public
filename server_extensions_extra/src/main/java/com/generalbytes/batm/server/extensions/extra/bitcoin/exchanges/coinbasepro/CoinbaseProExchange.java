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
package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.coinbasepro;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.common.currencies.FiatCurrency;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.XChangeExchange;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.dto.account.AccountInfo;
import org.knowm.xchange.dto.account.Wallet;

import java.util.HashSet;
import java.util.Set;

public class CoinbaseProExchange extends XChangeExchange {

    public CoinbaseProExchange(String preferredFiatCurrency) {
        super(getDefaultSpecification(), preferredFiatCurrency);
    }

    public CoinbaseProExchange(String key, String secret, String passphrase, String preferredFiatCurrency, boolean sandbox) {
        super(getSpecification(key, secret, passphrase, sandbox), preferredFiatCurrency);
    }

    private static ExchangeSpecification getDefaultSpecification() {
        return new org.knowm.xchange.coinbasepro.CoinbaseProExchange().getDefaultExchangeSpecification();
    }

    private static ExchangeSpecification getSpecification(String key, String secret, String passphrase, boolean sandbox) {
        ExchangeSpecification spec = getDefaultSpecification();
        spec.setApiKey(key);
        spec.setSecretKey(secret);
        spec.setExchangeSpecificParametersItem("passphrase", passphrase);
        spec.setExchangeSpecificParametersItem("Use_Sandbox", sandbox);
        return spec;
    }

    @Override
    public Set<String> getCryptoCurrencies() {
        Set<String> cryptoCurrencies = new HashSet<>();
        cryptoCurrencies.add(CryptoCurrency.ADA.getCode());
        cryptoCurrencies.add(CryptoCurrency.BCH.getCode());
        cryptoCurrencies.add(CryptoCurrency.BTC.getCode());
        cryptoCurrencies.add(CryptoCurrency.ETH.getCode());
        cryptoCurrencies.add(CryptoCurrency.LTC.getCode());
        cryptoCurrencies.add(CryptoCurrency.XRP.getCode());
        return cryptoCurrencies;
    }

    @Override
    public Set<String> getFiatCurrencies() {
        Set<String> fiatCurrencies = new HashSet<>();
        fiatCurrencies.add(FiatCurrency.EUR.getCode());
        fiatCurrencies.add(FiatCurrency.GBP.getCode());
        fiatCurrencies.add(FiatCurrency.USD.getCode());
        return fiatCurrencies;
    }

    @Override
    protected boolean isWithdrawSuccessful(String result) {
        return true;
    }

    @Override
    protected double getAllowedCallsPerSecond() {
        return 10;
    }

    @Override
    public Wallet getWallet(AccountInfo accountInfo, String currency) {
        return accountInfo.getWallet();
    }

//    public static void main(String[] args) {
//        CoinbaseProExchange rs = new CoinbaseProExchange("USD");
//        System.out.println(rs.getExchangeRateLast("BTC", "USD"));
//        System.out.println(rs.getExchangeRateForBuy("BTC", "USD"));
//        System.out.println(rs.getExchangeRateForSell("BTC", "USD"));
//        System.out.println(rs.calculateBuyPrice("BTC", "USD", new BigDecimal("100")));
//        System.out.println(rs.calculateSellPrice("BTC", "USD", new BigDecimal("100")));
//
//        CoinbaseProExchange xch = new CoinbaseProExchange("xx", "xx/xxxx==", "x", "EUR", true);
//        System.out.println(xch.getCryptoBalance("BTC"));
//        System.out.println(xch.getCryptoBalance("LTC"));
//        System.out.println(xch.getFiatBalance("USD"));
//        System.out.println(xch.getFiatBalance("EUR"));
//
//        System.out.println(xch.getDepositAddress("BTC"));
//        System.out.println(xch.getDepositAddress("ETH"));
//
//        System.out.println(xch.purchaseCoins(new BigDecimal("0.01"), "BTC", "USD", ""));
//        System.out.println(xch.sellCoins(new BigDecimal("0.01"), "BTC", "USD", ""));
//
//        System.out.println(xch.sendCoins("mswUGcPHp1YnkLCgF1TtoryqSc5E9Q8xFa", BigDecimal.ONE, "BTC", ""));
//    }
}