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
package com.generalbytes.batm.server.extensions.extra.tent;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.common.currencies.FiatCurrency;
import com.generalbytes.batm.server.extensions.*;
import com.generalbytes.batm.server.extensions.extra.tent.wallets.snowgemd.SnowgemRPCWallet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

public class TentExtension extends AbstractExtension {

    private static final Logger LOG = LoggerFactory.getLogger(TentExtension.class);

    @Override
    public String getName() {
        return "BATM TENT extra extension";
    }

    @Override
    public IWallet createWallet(String walletLogin, String tunnelPassword) {
        if (walletLogin != null && !walletLogin.trim().isEmpty()) {
            //"walletname:protocol:user:password:ip:port"
            StringTokenizer st = new StringTokenizer(walletLogin, ":");
            String walletName = st.nextToken();
            if ("snowgemd".equals(walletName)){
                String protocol = st.nextToken();
                String username = st.nextToken();
                String password = st.nextToken();
                String hostname = st.nextToken();
                String port = st.nextToken();

                if (protocol != null && username != null && password != null && hostname != null && port != null) {
                    String rpcURL = protocol + "://" + username + ":" + password + "@" + hostname + ":" + port;
                    return new SnowgemRPCWallet(rpcURL);
                }
            }

            if ("snowgemdemo".equalsIgnoreCase(walletName)){
                String fiatCurrency = st.nextToken();
                String walletAddress = "";
                if (st.hasMoreTokens()) {
                    walletAddress = st.nextToken();
                }

                if (fiatCurrency != null && walletAddress != null) {
                    return new DummyExchangeAndWalletAndSource(fiatCurrency, CryptoCurrency.TENT.getCode(), walletAddress);
                }
            }
        }

        return null;
    }

    @Override
    public ICryptoAddressValidator createAddressValidator(String cryptoCurrency) {
        if (CryptoCurrency.TENT.getCode().equalsIgnoreCase(cryptoCurrency)){
            return new TentAddressValidator();
        }
        return null;
    }

    @Override
    public IRateSource createRateSource(String sourceLogin) {
        if (sourceLogin != null && !sourceLogin.trim().isEmpty()){
            StringTokenizer st = new StringTokenizer(sourceLogin, ":");
            String exchangeType = st.nextToken();
            String preferredFiatCurrency = FiatCurrency.USD.getCode();
            if("tentfix".equalsIgnoreCase(exchangeType)){
                BigDecimal rate = BigDecimal.ZERO;
                if (st.hasMoreTokens()) {
                    try {
                        rate = new BigDecimal(st.nextToken());
                    } catch (Throwable e) {
                    }
                }
                if (st.hasMoreTokens()) {
                    preferredFiatCurrency = st.nextToken().toUpperCase();
                }
                return new FixPriceRateSource(rate, preferredFiatCurrency);
            }
        }
        return null;
    }

    @Override
    public Set<String> getSupportedCryptoCurrencies() {
        Set<String> result = new HashSet<>();
        result.add(CryptoCurrency.TENT.getCode());
        return result;
    }
}
