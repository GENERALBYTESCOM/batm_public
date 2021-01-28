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
package com.generalbytes.batm.server.extensions.extra.nano;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.common.currencies.FiatCurrency;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.binance.BinanceComExchange;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.binance.BinanceUsExchange;
import com.generalbytes.batm.server.extensions.extra.bitcoin.sources.coingecko.CoinGeckoRateSource;
import com.generalbytes.batm.server.extensions.extra.bitcoin.sources.coinpaprika.CoinPaprikaRateSource;
import com.generalbytes.batm.server.extensions.extra.dash.sources.coinmarketcap.CoinmarketcapRateSource;
import com.generalbytes.batm.server.extensions.extra.nano.wallets.nano_node.NanoRPCWallet;
import com.generalbytes.batm.server.extensions.extra.nano.wallets.paperwallet.NanoPaperWalletGenerator;
import com.generalbytes.batm.server.extensions.AbstractExtension;
import com.generalbytes.batm.server.extensions.FixPriceRateSource;
import com.generalbytes.batm.server.extensions.ICryptoAddressValidator;
import com.generalbytes.batm.server.extensions.ICryptoCurrencyDefinition;
import com.generalbytes.batm.server.extensions.IPaperWalletGenerator;
import com.generalbytes.batm.server.extensions.IRateSource;
import com.generalbytes.batm.server.extensions.IWallet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

public class NanoExtension extends AbstractExtension {
    private static final Logger log = LoggerFactory.getLogger(NanoExtension.class);

    private static final ICryptoCurrencyDefinition DEFINITION = new NanoDefinition();
    public static final String CURRENCY = CryptoCurrency.NANO.getCode();

    @Override
    public String getName() {
        return "BATM Nano extra extension";
    }

    @Override
    public IWallet createWallet(String walletLogin, String tunnelPassword) {
        try {
            if (walletLogin != null && !walletLogin.trim().isEmpty()) {
                StringTokenizer st = new StringTokenizer(walletLogin, ":");
                String walletType = st.nextToken();
                if ("nano_node".equalsIgnoreCase(walletType)) {
                    // "nano_node:protocol:ip:port:wallet:address"

                    String protocol = st.nextToken();

                    // Do special handling of ipv6 loopback address which has the delimiter
                    String hostname = st.nextToken();
                    if (hostname.equals("[")) {
                        st.nextToken(); // Just skip over it
                        hostname = "[::1]";
                    }

                    int port = Integer.parseInt(st.nextToken());
                    String walletId = "";
                    if (st.hasMoreTokens()) {
                        walletId = st.nextToken();
                    }
                    String account = "";
                    if (st.hasMoreTokens()) {
                        account = st.nextToken();
                    }

                    if (protocol != null && hostname != null && walletId != null && account != null) {
                        String rpcURL = protocol + "://" + hostname + ":" + port;
                        return new NanoRPCWallet(rpcURL, walletId, account);
                    }
                }
            }
        } catch (Exception e) {
            log.error("", e);
        }
        return null;
    }

    @Override
    public ICryptoAddressValidator createAddressValidator(String cryptoCurrency) {
        if (CryptoCurrency.NANO.getCode().equalsIgnoreCase(cryptoCurrency)) {
            return new NanoAddressValidator();
        }
        return null;
    }

    @Override
    public IRateSource createRateSource(String sourceLogin) {
        if (sourceLogin != null && !sourceLogin.trim().isEmpty()) {
            StringTokenizer st = new StringTokenizer(sourceLogin, ":");
            String rsType = st.nextToken();

            if ("coinmarketcap".equalsIgnoreCase(rsType)) {
                String preferredFiatCurrency = FiatCurrency.USD.getCode();
                String apiKey = null;
                if (st.hasMoreTokens()) {
                    preferredFiatCurrency = st.nextToken().toUpperCase();
                }
                if (st.hasMoreTokens()) {
                    apiKey = st.nextToken();
                }
                return new CoinmarketcapRateSource(apiKey, preferredFiatCurrency);
            } else if ("coingecko".equalsIgnoreCase(rsType)) {
                String preferredFiatCurrency = st.hasMoreTokens() ? st.nextToken().toUpperCase()
                        : FiatCurrency.USD.getCode();
                return new CoinGeckoRateSource(preferredFiatCurrency);
            } else if ("coinpaprika".equalsIgnoreCase(rsType)) {
                String preferredFiatCurrency = st.hasMoreTokens() ? st.nextToken().toUpperCase()
                        : FiatCurrency.USD.getCode();
                return new CoinPaprikaRateSource(preferredFiatCurrency);
            } else if ("binancecom".equalsIgnoreCase(rsType)) {
                String preferredFiatCurrency = FiatCurrency.EUR.getCode();
                if (st.hasMoreTokens()) {
                    preferredFiatCurrency = st.nextToken().toUpperCase();
                }
                return new BinanceComExchange(preferredFiatCurrency);
            } else if ("binanceus".equalsIgnoreCase(rsType)) {
                String preferredFiatCurrency = FiatCurrency.USD.getCode();
                if (st.hasMoreTokens()) {
                    preferredFiatCurrency = st.nextToken().toUpperCase();
                }
                return new BinanceUsExchange(preferredFiatCurrency);
            }

        }
        return null;
    }

    @Override
    public Set<String> getSupportedCryptoCurrencies() {
        Set<String> result = new HashSet<String>();
        result.add(CURRENCY);
        return result;
    }

    @Override
    public Set<ICryptoCurrencyDefinition> getCryptoCurrencyDefinitions() {
        Set<ICryptoCurrencyDefinition> result = new HashSet<>();
        result.add(DEFINITION);
        return result;
    }

    @Override
    public IPaperWalletGenerator createPaperWalletGenerator(String cryptoCurrency) {
        if (CryptoCurrency.NANO.getCode().equalsIgnoreCase(cryptoCurrency)) {
            return new NanoPaperWalletGenerator();
        }
        return null;
    }
}
