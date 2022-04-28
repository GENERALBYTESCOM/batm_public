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
package com.generalbytes.batm.server.extensions.extra.dash;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.common.currencies.FiatCurrency;
import com.generalbytes.batm.server.extensions.*;
import com.generalbytes.batm.server.extensions.FixPriceRateSource;
import com.generalbytes.batm.server.extensions.exceptions.helper.ExceptionHelper;
import com.generalbytes.batm.server.extensions.extra.dash.sources.cddash.CryptodiggersRateSource;
import com.generalbytes.batm.server.extensions.extra.dash.sources.coinmarketcap.CoinmarketcapRateSource;
import com.generalbytes.batm.server.extensions.extra.dash.wallets.dashd.DashRPCWallet;
import com.generalbytes.batm.server.extensions.extra.dash.wallets.dashd.DashUniqueAddressRPCWallet;
import com.generalbytes.batm.server.extensions.AbstractExtension;
import com.generalbytes.batm.server.extensions.ICryptoAddressValidator;
import com.generalbytes.batm.server.extensions.ICryptoCurrencyDefinition;
import com.generalbytes.batm.server.extensions.IPaperWalletGenerator;
import com.generalbytes.batm.server.extensions.IWallet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import java.math.BigDecimal;
import java.net.MalformedURLException;

public class DashExtension extends AbstractExtension{
    private static final Logger log = LoggerFactory.getLogger(DashExtension.class);

    private static final ICryptoCurrencyDefinition DEFINITION = new DashDefinition();
    public static final String CURRENCY = CryptoCurrency.DASH.getCode();

    @Override
    public String getName() {
        return "BATM Dash extra extension";
    }

    @Override
    public IWallet createWallet(String walletLogin, String tunnelPassword) {
        String walletType = null;
        try {
        if (walletLogin != null && !walletLogin.trim().isEmpty()) {
            StringTokenizer st = new StringTokenizer(walletLogin, ":");
            walletType = st.nextToken();
            if ("dashd".equalsIgnoreCase(walletType)
                || "dashdnoforward".equalsIgnoreCase(walletType)) {
                //"dashd:protocol:user:password:ip:port:accountname"

                String protocol = st.nextToken();
                String username = st.nextToken();
                String password = st.nextToken();
                String hostname = st.nextToken();
                int port = Integer.parseInt(st.nextToken());
                String accountName = "";
                if (st.hasMoreTokens()) {
                    accountName = st.nextToken();
                }

                InetSocketAddress tunnelAddress = ctx.getTunnelManager().connectIfNeeded(walletLogin, tunnelPassword, InetSocketAddress.createUnresolved(hostname, port));
                hostname = tunnelAddress.getHostString();
                port = tunnelAddress.getPort();

                try {
                    if (protocol != null && username != null && password != null && hostname != null && accountName != null) {
                        String rpcURL = protocol + "://" + username + ":" + password + "@" + hostname + ":" + port;
                        if ("dashdnoforward".equalsIgnoreCase(walletType)) {
                            return new DashUniqueAddressRPCWallet(rpcURL, accountName);
                        }
                        return new DashRPCWallet(rpcURL, accountName);
                    }
                } catch (MalformedURLException x) {
                    //swallow
                }
            }
        }
        } catch (Exception e) {
            String serialNumber = ExceptionHelper.findSerialNumberInStackTrace();
            log.warn("createWallet failed for prefix: {}, on terminal with serial number: {}", walletType, serialNumber);
        }
        return null;
    }

    @Override
    public ICryptoAddressValidator createAddressValidator(String cryptoCurrency) {
        if (CryptoCurrency.DASH.getCode().equalsIgnoreCase(cryptoCurrency)) {
            return new DashAddressValidator();
        }
        return null;
    }

    @Override
    public IRateSource createRateSource(String sourceLogin) {
        if (sourceLogin != null && !sourceLogin.trim().isEmpty()) {
            String exchangeType = null;
            try {
                StringTokenizer st = new StringTokenizer(sourceLogin, ":");
                exchangeType = st.nextToken();
                if ("cddash".equalsIgnoreCase(exchangeType)) {
                    if (st.hasMoreTokens()) {
                        return new CryptodiggersRateSource(st.nextToken().toUpperCase());
                    }
                    return new CryptodiggersRateSource(FiatCurrency.USD.getCode());
                } else if ("dashfix".equalsIgnoreCase(exchangeType)) {
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
                } else if ("coinmarketcap".equalsIgnoreCase(exchangeType)) {
                    String preferredFiatCurrency = FiatCurrency.USD.getCode();
                    String apiKey = null;
                    if (st.hasMoreTokens()) {
                        preferredFiatCurrency = st.nextToken().toUpperCase();
                    }
                    if (st.hasMoreTokens()) {
                        apiKey = st.nextToken();
                    }
                    return new CoinmarketcapRateSource(apiKey, preferredFiatCurrency);
                }
            } catch (Exception e) {
                String serialNumber = ExceptionHelper.findSerialNumberInStackTrace();
                log.warn("createRateSource failed for prefix: {}, on terminal with serial number: {}", exchangeType, serialNumber);
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
        if (CryptoCurrency.DASH.getCode().equalsIgnoreCase(cryptoCurrency)) {
            return new DashWalletGenerator("Xgb", ctx);
        }
        return null;
    }
}
