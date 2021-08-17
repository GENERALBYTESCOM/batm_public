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
package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.hitbtc;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.common.currencies.FiatCurrency;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.XChangeExchange;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.dto.account.AccountInfo;
import org.knowm.xchange.dto.account.Wallet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

public class HitbtcExchange extends XChangeExchange {
    private static final Logger log = LoggerFactory.getLogger("batm.master.exchange.HitbtcExchange");

    public HitbtcExchange(String preferredFiatCurrency) {
        super(getDefaultSpecification(), preferredFiatCurrency);
    }

    public HitbtcExchange(String clientKey, String clientSecret, String preferredFiatCurrency) {
        super(getSpecification(clientKey, clientSecret), preferredFiatCurrency);
    }


    private static ExchangeSpecification getDefaultSpecification() {
        return new org.knowm.xchange.hitbtc.v2.HitbtcExchange().getDefaultExchangeSpecification();
    }

    private static ExchangeSpecification getSpecification(String clientKey, String clientSecret) {
        ExchangeSpecification spec = getDefaultSpecification();
        spec.setApiKey(clientKey);
        spec.setSecretKey(clientSecret);
        return spec;
    }

    @Override
    public Set<String> getCryptoCurrencies() {
        Set<String> cryptoCurrencies = new HashSet<>();
        cryptoCurrencies.add(CryptoCurrency.BTC.getCode());
        cryptoCurrencies.add(CryptoCurrency.ETH.getCode());
        cryptoCurrencies.add(CryptoCurrency.LTC.getCode());
        cryptoCurrencies.add(CryptoCurrency.SMART.getCode());
        cryptoCurrencies.add(CryptoCurrency.BCH.getCode());
        cryptoCurrencies.add(CryptoCurrency.DOGE.getCode());
        cryptoCurrencies.add(CryptoCurrency.XMR.getCode());
        cryptoCurrencies.add(CryptoCurrency.NANO.getCode());
        cryptoCurrencies.add(CryptoCurrency.DASH.getCode());
        cryptoCurrencies.add(CryptoCurrency.NXT.getCode());
        return cryptoCurrencies;
    }

    @Override
    public Set<String> getFiatCurrencies() {
        Set<String> fiatCurrencies = new HashSet<>();
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
        return accountInfo.getWallet("Trading");
    }

//    public static void main(String[] args) {
//        HitbtcExchange xch = new HitbtcExchange("", "", "USD");
//        log.info(xch.getDepositAddress("XMR"));
//        log.info(xch.getExchangeRateForSell("XMR", "USD"));
//        //log.info(xch.sellCoins(BigDecimal.TEN, "XMR", "USD", ""));
//        log.info(xch.sendCoins("86didNu7QQdJvm1CAxpUCy9rJr7AcRLdz1xzSMEFio8DVknAu3PoLkY7VNoDBFdM2ZZ4kzfKyrHEUHrjRauXwSZGJ7SA7Ki", BigDecimal.TEN, "XMR", ""));
//    }
}