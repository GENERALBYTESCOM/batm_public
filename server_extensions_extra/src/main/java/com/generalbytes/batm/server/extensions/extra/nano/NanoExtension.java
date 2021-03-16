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
import com.generalbytes.batm.server.extensions.extra.nano.wallet.demo.DemoWallet;
import com.generalbytes.batm.server.extensions.extra.nano.wallet.node.NanoNodeWallet;
import com.generalbytes.batm.server.extensions.extra.nano.wallet.paper.NanoPaperWalletGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Set;
import java.util.StringTokenizer;

/*
 * EXTENSION NOTES:
 *
 * The NanoExtensionContext object which is passed around contains various different objects relating to the
 * cryptocurrency being used, including the crypto code identifier, address format and standard unit denomination.
 *
 * The context object also contains a reference to a globally-used NanoRpcClient instance. This is optionally
 * defined when the end user configures a nano_node wallet — if this is not defined, then certain actions will
 * become unavailable (or resort to less desirable fallbacks). This is a (messy) necessity due to the dependency
 * constraints put in place, so operations such as paper wallet generation and address validation must be processed
 * externally on an RPC endpoint.
 */
public class NanoExtension extends AbstractExtension {

    private static final Logger log = LoggerFactory.getLogger(NanoExtension.class);

    public static final CryptoCurrency CRYPTO = CryptoCurrency.NANO;

    private volatile NanoExtensionContext context = new NanoExtensionContext(CRYPTO, ctx, NanoCurrencyUtil.NANO);


    @Override
    public String getName() {
        return "BATM Nano extra extension";
    }

    @Override
    public void init(IExtensionContext ctx) {
        super.init(ctx);
        this.context = new NanoExtensionContext(CRYPTO, ctx, NanoCurrencyUtil.NANO);
    }

    @Override
    public IWallet createWallet(String walletLogin, String tunnelPassword) {
        try {
            if (walletLogin != null && !walletLogin.trim().isEmpty()) {
                StringTokenizer st = new StringTokenizer(walletLogin, ":");
                String walletName = st.nextToken();

                if ("nano_node".equalsIgnoreCase(walletName)) {
                    NanoNodeWallet wallet = NanoNodeWallet.create(context, st);
                    context.setRpcClient(wallet.getRpcClient()); // Set global RPC client
                    return wallet;
                } else if ("nano_demo".equalsIgnoreCase(walletName)) {
                    String fiatCurrency = st.nextToken();
                    String walletAddress = st.nextToken();
                    return new DemoWallet(fiatCurrency, CRYPTO.getCode(), walletAddress);
                }
            }
        } catch (Exception e) {
            log.error("Couldn't create wallet.", e);
        }
        return null;
    }

    @Override
    public ICryptoAddressValidator createAddressValidator(String cryptoCurrency) {
        if (CRYPTO.getCode().equalsIgnoreCase(cryptoCurrency))
            return new NanoAddressValidator(context);
        return null;
    }

    @Override
    public IRateSource createRateSource(String sourceLogin) {
        if (sourceLogin != null && !sourceLogin.trim().isEmpty()) {
            StringTokenizer st = new StringTokenizer(sourceLogin, ":");
            String sourceType = st.nextToken();

            if ("coinmarketcap".equalsIgnoreCase(sourceType)) {
                String preferredCurrency = getPreferredCurrency(st, FiatCurrency.USD);
                String apiKey = st.hasMoreTokens() ? st.nextToken() : null;
                return new CoinmarketcapRateSource(apiKey, preferredCurrency);
            } else if ("coingecko".equalsIgnoreCase(sourceType)) {
                return new CoinGeckoRateSource(getPreferredCurrency(st, FiatCurrency.USD));
            } else if ("coinpaprika".equalsIgnoreCase(sourceType)) {
                return new CoinPaprikaRateSource(getPreferredCurrency(st, FiatCurrency.USD));
            } else if ("binancecom".equalsIgnoreCase(sourceType)) {
                return new BinanceComExchange(getPreferredCurrency(st, FiatCurrency.EUR));
            } else if ("binanceus".equalsIgnoreCase(sourceType)) {
                return new BinanceUsExchange(getPreferredCurrency(st, FiatCurrency.USD));
            }
        }
        return null;
    }

    @Override
    public Set<String> getSupportedCryptoCurrencies() {
        return Collections.singleton(CRYPTO.getCode());
    }

    @Override
    public Set<ICryptoCurrencyDefinition> getCryptoCurrencyDefinitions() {
        return Collections.singleton(new NanoDefinition(new NanoPaymentSupport(context)));
    }

    @Override
    public IPaperWalletGenerator createPaperWalletGenerator(String cryptoCurrency) {
        if (CRYPTO.getCode().equalsIgnoreCase(cryptoCurrency))
            return new NanoPaperWalletGenerator(context);
        return null;
    }


    public static String getPreferredCurrency(StringTokenizer tokenizer, FiatCurrency defaultVal) {
        return tokenizer.hasMoreTokens() ? tokenizer.nextToken().toUpperCase() : defaultVal.getCode();
    }

}
