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
import com.generalbytes.batm.server.extensions.extra.nano.util.NanoUtil;
import com.generalbytes.batm.server.extensions.extra.nano.wallet.demo.DemoWallet;
import com.generalbytes.batm.server.extensions.extra.nano.wallet.node.NanoNodeWallet;
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
 */
public class NanoExtension extends AbstractExtension {

    private static final Logger log = LoggerFactory.getLogger(NanoExtension.class);

    public static final CryptoCurrency CRYPTO = CryptoCurrency.NANO;

    private volatile NanoExtensionContext context = new NanoExtensionContext(CRYPTO, ctx, NanoUtil.NANO);


    @Override
    public String getName() {
        return "BATM Nano extra extension";
    }

    @Override
    public void init(IExtensionContext ctx) {
        super.init(ctx);
        this.context = new NanoExtensionContext(CRYPTO, ctx, NanoUtil.NANO);
    }

    @Override
    public IWallet createWallet(String walletLogin, String tunnelPassword) {
        try {
            if (walletLogin != null && !walletLogin.trim().isEmpty()) {
                StringTokenizer st = new StringTokenizer(walletLogin, ":");
                String walletName = st.nextToken();

                if ("nano_node".equalsIgnoreCase(walletName)) {
                    return NanoNodeWallet.create(context, st);
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
    public Set<String> getSupportedCryptoCurrencies() {
        return Collections.singleton(CRYPTO.getCode());
    }

    @Override
    public Set<ICryptoCurrencyDefinition> getCryptoCurrencyDefinitions() {
        return Collections.singleton(new NanoDefinition(new NanoPaymentSupport(context)));
    }

}
