/*************************************************************************************
 * Copyright (C) 2014-2021 GENERAL BYTES s.r.o. All rights reserved.
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
package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.poloniex;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.XChangeExchange;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.dto.account.AccountInfo;
import org.knowm.xchange.dto.account.Wallet;

import java.util.HashSet;
import java.util.Set;

public class PoloniexExchange extends XChangeExchange {

    private static final Set<String> SUPPORTED_FIATS = new HashSet<>();
    private static final Set<String> SUPPORTED_CRYPTOS = new HashSet<>();

    static {
        SUPPORTED_FIATS.add(CryptoCurrency.BUSD.getCode());
        SUPPORTED_FIATS.add(CryptoCurrency.DAI.getCode());
        SUPPORTED_FIATS.add(CryptoCurrency.USDC.getCode());
        SUPPORTED_FIATS.add(CryptoCurrency.USDT.getCode());

        SUPPORTED_CRYPTOS.add(CryptoCurrency.BCH.getCode());
        SUPPORTED_CRYPTOS.add(CryptoCurrency.BTC.getCode());
        SUPPORTED_CRYPTOS.add(CryptoCurrency.BURST.getCode());
        SUPPORTED_CRYPTOS.add(CryptoCurrency.ETH.getCode());
        SUPPORTED_CRYPTOS.add(CryptoCurrency.LTC.getCode());
        SUPPORTED_CRYPTOS.add(CryptoCurrency.DASH.getCode());
        SUPPORTED_CRYPTOS.add(CryptoCurrency.DAI.getCode());
        SUPPORTED_CRYPTOS.add(CryptoCurrency.DGB.getCode());
        SUPPORTED_CRYPTOS.add(CryptoCurrency.DOGE.getCode());
        SUPPORTED_CRYPTOS.add(CryptoCurrency.LSK.getCode());
        SUPPORTED_CRYPTOS.add(CryptoCurrency.NXT.getCode());
        SUPPORTED_CRYPTOS.add(CryptoCurrency.POT.getCode());
        SUPPORTED_CRYPTOS.add(CryptoCurrency.REP.getCode());
        SUPPORTED_CRYPTOS.add(CryptoCurrency.SYS.getCode());
        SUPPORTED_CRYPTOS.add(CryptoCurrency.VIA.getCode());
        SUPPORTED_CRYPTOS.add(CryptoCurrency.XMR.getCode());
        SUPPORTED_CRYPTOS.add(CryptoCurrency.XRP.getCode());
        SUPPORTED_CRYPTOS.add(CryptoCurrency.XPM.getCode());
    }

    public PoloniexExchange(String preferredFiatCurrency) {
        super(getDefaultSpecification(), preferredFiatCurrency);
    }

    public PoloniexExchange(String key, String secret, String preferredFiatCurrency) {
        super(getSpecification(key, secret), preferredFiatCurrency);
    }

    private static ExchangeSpecification getDefaultSpecification() {
        return new org.knowm.xchange.poloniex.PoloniexExchange().getDefaultExchangeSpecification();
    }

    private static ExchangeSpecification getSpecification(String key, String secret) {
        ExchangeSpecification spec = getDefaultSpecification();
        spec.setApiKey(key);
        spec.setSecretKey(secret);
        return spec;
    }

    @Override
    public Set<String> getCryptoCurrencies() {
        return SUPPORTED_CRYPTOS;
    }

    @Override
    public Set<String> getFiatCurrencies() {
        return SUPPORTED_FIATS;
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
}
