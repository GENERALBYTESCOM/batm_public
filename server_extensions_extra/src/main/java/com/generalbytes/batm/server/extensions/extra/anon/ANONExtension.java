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
package com.generalbytes.batm.server.extensions.extra.anon;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.common.currencies.FiatCurrency;
import com.generalbytes.batm.server.extensions.*;
import com.generalbytes.batm.server.extensions.FixPriceRateSource;
import com.generalbytes.batm.server.extensions.ExtensionsUtil;
import com.generalbytes.batm.server.extensions.extra.anon.wallets.anond.ANONRPCWallet;
import com.generalbytes.batm.server.extensions.extra.dash.sources.coinmarketcap.CoinmarketcapRateSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * Created by ANON Core Developers on 8/25/18.
 */
public class ANONExtension extends AbstractExtension {

    @Override
    public String getName() {
        return "BATM ANON extra extension";
    }

    @Override
    public IWallet createWallet(String walletLogin, String tunnelPassword) {
        if (walletLogin != null && !walletLogin.trim().isEmpty()) {
            try {
                StringTokenizer st = new StringTokenizer(walletLogin, ":");
                String walletType = st.nextToken();

                if ("anond".equalsIgnoreCase(walletType)) {
                    String protocol = st.nextToken();
                    String username = st.nextToken();
                    String password = st.nextToken();
                    String hostname = st.nextToken();
                    String port = st.nextToken();
                    String accountName = "";
                    if (st.hasMoreTokens()) {
                        accountName = st.nextToken();
                    }


                    if (protocol != null && username != null && password != null && hostname != null && port != null && accountName != null) {
                        String rpcURL = protocol + "://" + username + ":" + password + "@" + hostname + ":" + port;
                        return new ANONRPCWallet(rpcURL, accountName);
                    }
                }
                if ("anondemo".equalsIgnoreCase(walletType)) {

                    String fiatCurrency = st.nextToken();
                    String walletAddress = "";
                    if (st.hasMoreTokens()) {
                        walletAddress = st.nextToken();
                    }

                    if (fiatCurrency != null && walletAddress != null) {
                        return new DummyExchangeAndWalletAndSource(fiatCurrency, CryptoCurrency.ANON.getCode(), walletAddress);
                    }
                }
            } catch (Exception e) {
                ExtensionsUtil.logExtensionParamsException("createWallet", walletLogin, e);
            }
        }
        return null;
    }

    @Override
    public ICryptoAddressValidator createAddressValidator(String cryptoCurrency) {
        if (CryptoCurrency.ANON.getCode().equalsIgnoreCase(cryptoCurrency)) {
            return new ANONAddressValidator();
        }
        return null;
    }

    @Override
    public IRateSource createRateSource(String sourceLogin) {
        if (sourceLogin != null && !sourceLogin.trim().isEmpty()) {
            try {
                StringTokenizer st = new StringTokenizer(sourceLogin, ":");
                String exchangeType = st.nextToken();
                if ("coinmarketcap".equalsIgnoreCase(exchangeType)) {
                    String preferredFiatCurrency = FiatCurrency.USD.getCode();
                    String apiKey = null;
                    if (st.hasMoreTokens()) {
                        preferredFiatCurrency = st.nextToken().toUpperCase();
                    }
                    if (st.hasMoreTokens()) {
                        apiKey = st.nextToken();
                    }
                    return new CoinmarketcapRateSource(apiKey, preferredFiatCurrency);
                } else if ("anonfix".equalsIgnoreCase(exchangeType)) {
                    BigDecimal rate = BigDecimal.ZERO;
                    if (st.hasMoreTokens()) {
                        try {
                            rate = new BigDecimal(st.nextToken());
                        } catch (Throwable e) {
                        }
                    }
                    String preferedFiatCurrency = FiatCurrency.USD.getCode();
                    if (st.hasMoreTokens()) {
                        preferedFiatCurrency = st.nextToken().toUpperCase();
                    }
                    return new FixPriceRateSource(rate, preferedFiatCurrency);
                }
            } catch (Exception e) {
                ExtensionsUtil.logExtensionParamsException("createRateSource", sourceLogin, e);
            }
        }
        return null;
    }

    @Override
    public Set<String> getSupportedCryptoCurrencies() {
        Set<String> result = new HashSet<>();
        result.add(CryptoCurrency.ANON.getCode());
        return result;
    }

}

