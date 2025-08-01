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
package com.generalbytes.batm.server.extensions.extra.tron;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.server.extensions.AbstractExtension;
import com.generalbytes.batm.server.extensions.ExtensionsUtil;
import com.generalbytes.batm.server.extensions.ICryptoAddressValidator;
import com.generalbytes.batm.server.extensions.ICryptoCurrencyDefinition;
import com.generalbytes.batm.server.extensions.IExchange;
import com.generalbytes.batm.server.extensions.IWallet;
import com.generalbytes.batm.server.extensions.util.DummyWalletAndExchangeAndSourceFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.StringTokenizer;

public class TronExtension extends AbstractExtension {

    private static final Collection<String> supportedCryptoCurrencies = Set.of(
        CryptoCurrency.TRX.getCode(),
        CryptoCurrency.USDTTRON.getCode()
    );
    private static final Set<ICryptoCurrencyDefinition> cryptoCurrencyDefinitions = Collections.singleton(new UsdttronDefinition());
    private static final DummyWalletAndExchangeAndSourceFactory dummyFactory = new DummyWalletAndExchangeAndSourceFactory();

    @Override
    public String getName() {
        return "BATM Tron extension";
    }

    @Override
    public IWallet createWallet(String walletLogin, String tunnelPassword) {
        if (walletLogin != null && !walletLogin.trim().isEmpty()) {
            try {
                StringTokenizer st = new StringTokenizer(walletLogin, ":");
                String walletType = st.nextToken();

                if (walletType.startsWith("tridentTRC20_")) {
                    StringTokenizer wt = new StringTokenizer(walletType, "_");
                    wt.nextToken();
                    String tokenSymbol = wt.nextToken();
                    int tokenDecimalPlaces = Integer.parseInt(wt.nextToken());
                    String contractAddress = wt.nextToken();

                    String tronProApiKey = st.nextToken();
                    String hexPrivateKey = st.nextToken();
                    int feeLimitTrx = st.hasMoreTokens() ? Integer.parseInt(st.nextToken()) : 30;
                    return new TRC20Wallet(tronProApiKey, hexPrivateKey, tokenSymbol, tokenDecimalPlaces, contractAddress, feeLimitTrx);
                } else if ("usdttrondemo".equalsIgnoreCase(walletType)) {
                    return dummyFactory.createDummyWithFiatCurrencyAndAddress(st, CryptoCurrency.USDTTRON);
                }
            } catch (Exception e) {
                ExtensionsUtil.logExtensionParamsException("createWallet", getClass().getSimpleName(), walletLogin, e);
            }
        }
        return null;
    }

    @Override
    public IExchange createExchange(String exchangeLogin) {
        if (exchangeLogin == null || exchangeLogin.isBlank()) {
            return null;
        }

        try {
            StringTokenizer st = new StringTokenizer(exchangeLogin, ":");
            String exchangeType = st.nextToken();

            if ("usdttrondemo".equalsIgnoreCase(exchangeType)) {
                return dummyFactory.createDummyWithFiatCurrencyAndAddress(st, CryptoCurrency.USDTTRON);
            }
        } catch (Exception e) {
            ExtensionsUtil.logExtensionParamsException("createExtension", getClass().getSimpleName(), exchangeLogin, e);
        }
        return null;
    }

    @Override
    public ICryptoAddressValidator createAddressValidator(String cryptoCurrency) {
        if (!supportedCryptoCurrencies.contains(cryptoCurrency)) {
            return null;
        }

        return new TronCryptoAddressValidator();
    }

    @Override
    public Set<ICryptoCurrencyDefinition> getCryptoCurrencyDefinitions() {
        return cryptoCurrencyDefinitions;
    }
}
