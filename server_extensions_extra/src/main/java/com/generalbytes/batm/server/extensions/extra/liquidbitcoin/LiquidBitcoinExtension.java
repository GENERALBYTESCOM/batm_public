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
package com.generalbytes.batm.server.extensions.extra.liquidbitcoin;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.common.currencies.FiatCurrency;
import com.generalbytes.batm.server.extensions.AbstractExtension;
import com.generalbytes.batm.server.extensions.ExtensionsUtil;
import com.generalbytes.batm.server.extensions.FixPriceRateSource;
import com.generalbytes.batm.server.extensions.ICryptoAddressValidator;
import com.generalbytes.batm.server.extensions.ICryptoCurrencyDefinition;
import com.generalbytes.batm.server.extensions.IRateSource;
import com.generalbytes.batm.server.extensions.IWallet;
import com.generalbytes.batm.server.extensions.extra.liquidbitcoin.wallets.elementsd.ElementsdRPCWalletWithUniqueAddresses;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

@Slf4j
public class LiquidBitcoinExtension extends AbstractExtension {
    private static final ICryptoCurrencyDefinition DEFINITION = new LiquidBitcoinDefinition();

    @Override
    public String getName() {
        return "BATM Liquid Network Bitcoin extension";
    }

    @Override
    public IWallet createWallet(String walletLogin, String tunnelPassword) {
        try {
            if (walletLogin != null && !walletLogin.isBlank()) {
                StringTokenizer st = new StringTokenizer(walletLogin, ":");
                String walletType = st.nextToken();

                if ("elementsdbtcnoforward".equalsIgnoreCase(walletType)) {
                    //"elementsdbtcnoforward:protocol:user:password:ip:port:walletname"

                    String protocol = st.nextToken();
                    String username = st.nextToken();
                    String password = st.nextToken();
                    String hostname = st.nextToken();
                    int port = Integer.parseInt(st.nextToken());
                    String walletName = "";
                    if (st.hasMoreTokens()) {
                        walletName = st.nextToken();
                    }

                    if (ctx != null) {
                        InetSocketAddress tunnelAddress = ctx.getTunnelManager().connectIfNeeded(walletLogin, tunnelPassword, InetSocketAddress.createUnresolved(hostname, port));
                        hostname = tunnelAddress.getHostString();
                        port = tunnelAddress.getPort();
                    }

                    if (protocol != null && username != null && password != null && hostname != null && walletName != null) {
                        String rpcURL = protocol + "://" + username + ":" + password + "@" + hostname + ":" + port;
                        return new ElementsdRPCWalletWithUniqueAddresses(rpcURL, walletName);
                    }
                }
            }
        } catch (Exception e) {
            ExtensionsUtil.logExtensionParamsException("createWallet", getClass().getSimpleName(), walletLogin, e);
        }
        return null;
    }

    @Override
    public ICryptoAddressValidator createAddressValidator(String cryptoCurrency) {
        if (CryptoCurrency.L_BTC.getCode().equalsIgnoreCase(cryptoCurrency)) {
            return new LiquidBitcoinAddressValidator();
        }
        return null;
    }

    @Override
    public IRateSource createRateSource(String sourceLogin) {
        if (sourceLogin != null && !sourceLogin.isBlank()) {
            try {
                StringTokenizer st = new StringTokenizer(sourceLogin, ":");
                String exchangeType = st.nextToken();

                if ("l_btcfix".equalsIgnoreCase(exchangeType)) {
                    BigDecimal rate = getRate(st);
                    String preferredFiatCurrency = getPreferredFiatCurrency(st);
                    return new FixPriceRateSource(rate, preferredFiatCurrency);
                }
            } catch (Exception e) {
                ExtensionsUtil.logExtensionParamsException("createRateSource", getClass().getSimpleName(), sourceLogin, e);
            }

        }
        return null;
    }

    private static String getPreferredFiatCurrency(StringTokenizer st) {
        String preferedFiatCurrency = FiatCurrency.USD.getCode();
        if (st.hasMoreTokens()) {
            preferedFiatCurrency = st.nextToken().toUpperCase();
        }
        return preferedFiatCurrency;
    }

    private static BigDecimal getRate(StringTokenizer st) {
        BigDecimal rate = BigDecimal.ZERO;
        if (st.hasMoreTokens()) {
            try {
                rate = new BigDecimal(st.nextToken());
            } catch (Exception e) {
                log.warn("Failed to get fix rate for L-BTC, using default 0", e);
            }
        }
        return rate;
    }

    @Override
    public Set<String> getSupportedCryptoCurrencies() {
        Set<String> result = new HashSet<>();
        result.add(CryptoCurrency.L_BTC.getCode());
        return result;
    }

    @Override
    public Set<ICryptoCurrencyDefinition> getCryptoCurrencyDefinitions() {
        Set<ICryptoCurrencyDefinition> result = new HashSet<>();
        result.add(DEFINITION);
        return result;
    }

}
