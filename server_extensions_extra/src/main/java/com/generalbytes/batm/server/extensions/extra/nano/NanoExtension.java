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
import com.generalbytes.batm.server.extensions.extra.nano.wallets.node.NanoRPCClient;
import com.generalbytes.batm.server.extensions.extra.nano.wallets.node.NanoNodeWallet;
import com.generalbytes.batm.server.extensions.extra.nano.wallets.node.NanoWSClient;
import com.generalbytes.batm.server.extensions.extra.nano.wallets.paper.NanoPaperWalletGenerator;
import com.generalbytes.batm.server.extensions.AbstractExtension;
import com.generalbytes.batm.server.extensions.ICryptoAddressValidator;
import com.generalbytes.batm.server.extensions.ICryptoCurrencyDefinition;
import com.generalbytes.batm.server.extensions.IPaperWalletGenerator;
import com.generalbytes.batm.server.extensions.IRateSource;
import com.generalbytes.batm.server.extensions.IWallet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

public class NanoExtension extends AbstractExtension {

    private static final Logger log = LoggerFactory.getLogger(NanoExtension.class);

    public static final String CURRENCY_CODE = CryptoCurrency.NANO.getCode();
    private static final ICryptoCurrencyDefinition DEFINITION = new NanoDefinition();



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
                    /*
                     * ORDER OF CONFIGURATION PARAMETERS (split by colon):
                     * 0 | "nano_node" (protocol name)
                     * 1 | RPC protocol (http/https)
                     * 2 | RPC IP or host
                     * 3 | RPC port
                     * 4 | Websocket protocol (ws/wss)
                     * 5 | Websocket IP or host
                     * 6 | Websocket port
                     * 7 | Hot wallet ID
                     * 8 | Hot wallet address
                     */


                    // RPC CLIENT
                    String rpcProtocol = st.nextToken();
                    // Do special handling of ipv6 loopback address which has the delimiter
                    String rpcHost = st.nextToken();
                    if (rpcHost.equals("[")) {
                        st.nextToken();
                        rpcHost = "[::1]";
                    }
                    int rpcPort = Integer.parseInt(st.nextToken());

                    // WEBSOCKET CLIENT
                    String wsProtocol = st.nextToken();
                    String wsHost = st.nextToken();
                    if (wsHost.equals("[")) {
                        st.nextToken();
                        wsHost = "[::1]";
                    }
                    String wsPortStr = st.nextToken();
                    int wsPort = wsPortStr.isEmpty() ? -1 : Integer.parseInt(wsPortStr);

                    // WALLET & ACCOUNT
                    String walletId = null, account = null;
                    if (st.hasMoreTokens())
                        walletId = st.nextToken();
                    if (st.hasMoreTokens())
                        account = st.nextToken();


                    NanoRPCClient rpcClient = new NanoRPCClient(new URL(rpcProtocol, rpcHost, rpcPort, ""));
                    NanoWSClient wsClient = null;
                    if (!wsProtocol.isEmpty() && !wsHost.isEmpty() && wsPort != -1) {
                        wsClient = new NanoWSClient(new URI(wsProtocol, "", wsHost, wsPort, "", "", ""));
                    }
                    return new NanoNodeWallet(rpcClient, wsClient, walletId, account);
                }
            }
        } catch (Exception e) {
            log.error("Couldn't create wallet.", e);
        }
        return null;
    }

    @Override
    public ICryptoAddressValidator createAddressValidator(String cryptoCurrency) {
        if (CURRENCY_CODE.equalsIgnoreCase(cryptoCurrency))
            return new NanoAddressValidator();
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
        Set<String> result = new HashSet<>();
        result.add(CURRENCY_CODE);
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
        if (CURRENCY_CODE.equalsIgnoreCase(cryptoCurrency))
            return new NanoPaperWalletGenerator();
        return null;
    }

}
