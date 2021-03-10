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
package com.generalbytes.batm.server.extensions.extra.nano;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.common.currencies.FiatCurrency;
import com.generalbytes.batm.server.extensions.*;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.binance.BinanceComExchange;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.binance.BinanceUsExchange;
import com.generalbytes.batm.server.extensions.extra.bitcoin.sources.coingecko.CoinGeckoRateSource;
import com.generalbytes.batm.server.extensions.extra.bitcoin.sources.coinpaprika.CoinPaprikaRateSource;
import com.generalbytes.batm.server.extensions.extra.dash.sources.coinmarketcap.CoinmarketcapRateSource;
import com.generalbytes.batm.server.extensions.extra.potcoin.PotcoinAddressValidator;
import com.generalbytes.batm.server.extensions.extra.potcoin.wallets.potwallet.Potwallet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.*;

public class NanoExtension extends AbstractExtension {

    public static final String CODE = CryptoCurrency.NANO.getCode();

    private static final Logger log = LoggerFactory.getLogger(NanoExtension.class);


    @Override
    public String getName() {
        return "BATM Nano extra extension";
    }

    @Override
    public IWallet createWallet(String walletLogin, String tunnelPassword) {
        return null; //todo
    }

    @Override
    public ICryptoAddressValidator createAddressValidator(String cryptoCurrency) {
        if (CODE.equalsIgnoreCase(cryptoCurrency))
            return new NanoAddressValidator();
        return null;
    }

    @Override
    public IRateSource createRateSource(String sourceLogin) {
        if (sourceLogin != null && !sourceLogin.trim().isEmpty()) {
            StringTokenizer st = new StringTokenizer(sourceLogin, ":");
            String rsType = st.nextToken();

            if ("coinmarketcap".equalsIgnoreCase(rsType)) {
                String preferredFiatCurrency = st.hasMoreTokens()
                        ? st.nextToken().toUpperCase() : FiatCurrency.USD.getCode();
                String apiKey = st.hasMoreTokens() ? st.nextToken() : null;
                return new CoinmarketcapRateSource(apiKey, preferredFiatCurrency);
            } else if ("coingecko".equalsIgnoreCase(rsType)) {
                String preferredFiatCurrency = st.hasMoreTokens()
                        ? st.nextToken().toUpperCase() : FiatCurrency.USD.getCode();
                return new CoinGeckoRateSource(preferredFiatCurrency);
            } else if ("coinpaprika".equalsIgnoreCase(rsType)) {
                String preferredFiatCurrency = st.hasMoreTokens()
                        ? st.nextToken().toUpperCase() : FiatCurrency.USD.getCode();
                return new CoinPaprikaRateSource(preferredFiatCurrency);
            } else if ("binancecom".equalsIgnoreCase(rsType)) {
                String preferredFiatCurrency = st.hasMoreTokens()
                        ? st.nextToken().toUpperCase() : FiatCurrency.EUR.getCode();
                return new BinanceComExchange(preferredFiatCurrency);
            } else if ("binanceus".equalsIgnoreCase(rsType)) {
                String preferredFiatCurrency = st.hasMoreTokens()
                        ? st.nextToken().toUpperCase() : FiatCurrency.USD.getCode();
                return new BinanceUsExchange(preferredFiatCurrency);
            }

        }
        return null;
    }

    @Override
    public Set<String> getSupportedCryptoCurrencies() {
        return Collections.singleton(CODE);
    }

    @Override
    public Set<ICryptoCurrencyDefinition> getCryptoCurrencyDefinitions() {
        return Collections.singleton(new NanoDefinition());
    }

    @Override
    public IPaperWalletGenerator createPaperWalletGenerator(String cryptoCurrency) {
//        if (CODE.equalsIgnoreCase(cryptoCurrency)) todo
//            return new NanoPaperWalletGenerator(ctx, CURRENCY_SPEC);
        return null;
    }

}
