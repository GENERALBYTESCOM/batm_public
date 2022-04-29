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
package com.generalbytes.batm.server.extensions.extra.zcoin;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.common.currencies.FiatCurrency;
import com.generalbytes.batm.server.extensions.*;
import com.generalbytes.batm.server.extensions.FixPriceRateSource;
import com.generalbytes.batm.server.extensions.ExtensionsUtil;
import com.generalbytes.batm.server.extensions.extra.dash.sources.coinmarketcap.CoinmarketcapRateSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

public class ZcoinExtension extends AbstractExtension {

    private static final Logger log = LoggerFactory.getLogger(ZcoinExtension.class);

    @Override
    public String getName() { return "BATM Zcoin extension"; }

    @Override
    public IWallet createWallet(String walletLogin, String tunnelPassword) {
        if (walletLogin != null && !walletLogin.trim().isEmpty()) {
            try {
                StringTokenizer st = new StringTokenizer(walletLogin, ":");
                String walletType = st.nextToken();

                if ("zcoind".equalsIgnoreCase(walletType)) {
                    String protocol = st.nextToken();
                    String username = st.nextToken();
                    String password = st.nextToken();
                    String hostname = st.nextToken();
                    String port = st.nextToken();
                    String accountName = "";
                    if (st.hasMoreTokens()) {
                        accountName = st.nextToken();
                    }

                    if (protocol != null &&
                        username != null &&
                        password != null &&
                        hostname != null &&
                        port != null &&
                        accountName != null) {
                        String rpcURL = protocol + "://" + username + ":" + password
                            + "@" + hostname + ":" + port;
                    }
                }
                if ("xzcdemo".equalsIgnoreCase(walletType)) {

                    String fiatCurrency = st.nextToken();
                    String walletAddress = "";
                    if (st.hasMoreTokens()) {
                        walletAddress = st.nextToken();
                    }

                    if (fiatCurrency != null && walletAddress != null) {
                        return new DummyExchangeAndWalletAndSource(fiatCurrency, CryptoCurrency.XZC.getCode(), walletAddress);
                    }
                }
            } catch (Exception e) {
                log.warn("createWallet failed for prefix: {}", ExtensionsUtil.getPrefixWithCountOfParameters(walletLogin));
            }
        }
        return  null;
    }

    @Override
    public ICryptoAddressValidator createAddressValidator(String cryptoCurrency) {
        if (CryptoCurrency.XZC.getCode().equalsIgnoreCase(cryptoCurrency)) {
            return new ZcoinAddressValidator();
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
                } else if ("zcoinfix".equalsIgnoreCase(exchangeType)) {
                    BigDecimal rate = BigDecimal.ZERO;
                    if (st.hasMoreTokens()) {
                        try {
                            rate = new BigDecimal(st.nextToken());
                        } catch (Throwable e) {

                        }
                    }
                    String preferredFiatCurrency = FiatCurrency.USD.getCode();
                    if (st.hasMoreTokens()) {
                        preferredFiatCurrency = st.nextToken().toUpperCase();
                    }
                    return new FixPriceRateSource(rate, preferredFiatCurrency);
                }
            } catch (Exception e) {
                log.warn("createRateSource failed for prefix: {} ", ExtensionsUtil.getPrefixWithCountOfParameters(sourceLogin));
            }
        }
        return null;
    }

    @Override
    public Set<String> getSupportedCryptoCurrencies() {
        Set<String> result = new HashSet<>();
        result.add(CryptoCurrency.XZC.getCode());
        return result;
    }
}
