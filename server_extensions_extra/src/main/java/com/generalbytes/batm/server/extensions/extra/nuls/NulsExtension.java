/*
 *
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
 */
package com.generalbytes.batm.server.extensions.extra.nuls;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.common.currencies.FiatCurrency;
import com.generalbytes.batm.server.extensions.*;
import com.generalbytes.batm.server.extensions.extra.lisk.sources.binance.BinanceRateSource;
import com.generalbytes.batm.server.extensions.extra.lisk.wallets.liskbinancewallet.BinanceWallet;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * @author naveen
 */
public class NulsExtension extends AbstractExtension {

    @Override
    public String getName() {
        return "BATM NULS extra extension";
    }

    @Override
    public IWallet createWallet(String walletLogin) {
        if (walletLogin !=null && !walletLogin.trim().isEmpty()) {
            StringTokenizer st = new StringTokenizer(walletLogin,":");
            String walletType = st.nextToken();

            if ("nulsBinance".equalsIgnoreCase(walletType)) {
                String address = st.nextToken();
                String binanceApiKey = st.nextToken();
                String binanceApiSecret = st.nextToken();
                if (address != null && binanceApiKey !=null && binanceApiSecret != null ) {
                    return new BinanceWallet(address,binanceApiKey,binanceApiSecret,CryptoCurrency.NULS.getCode());
                }
            }
            if ("nulsDemo".equalsIgnoreCase(walletType)) {
                String fiatCurrency = st.nextToken();
                String walletAddress = "";
                if (st.hasMoreTokens()) {
                    walletAddress = st.nextToken();
                }
                if (fiatCurrency != null && walletAddress != null) {
                    return new DummyExchangeAndWalletAndSource(fiatCurrency, CryptoCurrency.NULS.getCode(), walletAddress);
                }
            }
        }
        return null;
    }

    @Override
    public ICryptoAddressValidator createAddressValidator(String cryptoCurrency) {
        if (CryptoCurrency.NULS.getCode().equalsIgnoreCase(cryptoCurrency)) {
            return new NulsAddressValidator();
        }
        return null;
    }

    @Override
    public IRateSource createRateSource(String sourceLogin) {
        if (sourceLogin != null && !sourceLogin.trim().isEmpty()) {
            StringTokenizer st = new StringTokenizer(sourceLogin, ":");
            String exchangeType = st.nextToken();
            if ("nulsFix".equalsIgnoreCase(exchangeType)) {
                BigDecimal rate = BigDecimal.ZERO;
                if (st.hasMoreTokens()) {
                    try {
                        rate = new BigDecimal(st.nextToken());
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }
                String preferredFiatCurrency = FiatCurrency.USD.getCode();
                if (st.hasMoreTokens()) {
                    preferredFiatCurrency = st.nextToken().toUpperCase();
                }
                return new FixPriceRateSource(rate, preferredFiatCurrency);
            }
            else if ("nulsBinanceRateSource".equalsIgnoreCase(exchangeType)) {
                String preferredFiatCurrency = FiatCurrency.USD.getCode();
                String coinMarketCapApiKey = null;
                if (st.hasMoreTokens()) {
                    preferredFiatCurrency = st.nextToken().toUpperCase();
                }
                if (st.hasMoreTokens()) {
                    coinMarketCapApiKey = st.nextToken();
                }
                return new BinanceRateSource(preferredFiatCurrency, coinMarketCapApiKey);
            }
        }
        return null;
    }

    @Override
    public Set<String> getSupportedCryptoCurrencies() {
        Set<String> result = new HashSet<>();
        result.add(CryptoCurrency.HATCH.getCode());
        result.add(CryptoCurrency.NULS.getCode());
        return result;
    }
}
