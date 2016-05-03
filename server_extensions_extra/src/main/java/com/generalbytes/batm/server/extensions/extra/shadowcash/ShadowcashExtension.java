/*************************************************************************************
 * Copyright (C) 2014-2016 GENERAL BYTES s.r.o. All rights reserved.
 * <p/>
 * This software may be distributed and modified under the terms of the GNU
 * General Public License version 2 (GPL2) as published by the Free Software
 * Foundation and appearing in the file GPL2.TXT included in the packaging of
 * this file. Please note that GPL2 Section 2[b] requires that all works based
 * on this software must also be made publicly available under the terms of
 * the GPL2 ("Copyleft").
 * <p/>
 * Contact information
 * -------------------
 * <p/>
 * GENERAL BYTES s.r.o.
 * Web      :  http://www.generalbytes.com
 ************************************************************************************/
package com.generalbytes.batm.server.extensions.extra.shadowcash;

import com.generalbytes.batm.server.extensions.ICryptoAddressValidator;
import com.generalbytes.batm.server.extensions.ICurrencies;
import com.generalbytes.batm.server.extensions.IExchange;
import com.generalbytes.batm.server.extensions.IExtension;
import com.generalbytes.batm.server.extensions.IPaperWalletGenerator;
import com.generalbytes.batm.server.extensions.IPaymentProcessor;
import com.generalbytes.batm.server.extensions.IRateSource;
import com.generalbytes.batm.server.extensions.IWallet;
import com.generalbytes.batm.server.extensions.extra.shadowcash.sources.FixPriceRateSource;
import com.generalbytes.batm.server.extensions.extra.shadowcash.sources.bittrex.BittrexRateSource;
import com.generalbytes.batm.server.extensions.extra.shadowcash.sources.poloniex.PoloniexRateSource;
import com.generalbytes.batm.server.extensions.extra.shadowcash.wallets.paperwallet.ShadowcashPaperWalletGenerator;
import com.generalbytes.batm.server.extensions.extra.shadowcash.wallets.shadowcashd.ShadowcashdRPCWallet;
import com.generalbytes.batm.server.extensions.watchlist.IWatchList;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

@Slf4j
public class ShadowcashExtension implements IExtension {

    @Override
    public String getName() {
        return "BATM Shadowcash extension";
    }

    @Override
    public IExchange createExchange(String exchangeLogin) {
        return null; // TODO: add exchange
    }

    @Override
    public IPaymentProcessor createPaymentProcessor(String paymentProcessorLogin) {
        return null;
    }

    @Override
    public IWallet createWallet(String walletLogin) {

        System.out.println("createWallet, walletLogin: " + walletLogin);

        if (walletLogin != null && !walletLogin.trim().isEmpty()) {
            StringTokenizer st = new StringTokenizer(walletLogin, ":");
            String walletType = st.nextToken();

            if ("shadowcashd".equalsIgnoreCase(walletType)) {
                String protocol = st.nextToken();
                String username = st.nextToken();
                String password = st.nextToken();
                String hostname = st.nextToken();
                String port = st.nextToken();

                if (protocol != null && username != null && password != null && hostname != null && port != null) {
                    String rpcURL = protocol + "://" + hostname + ":" + port;
                    return new ShadowcashdRPCWallet(rpcURL, username, password);
                }
            }

        }
        return null;
    }

    @Override
    public ICryptoAddressValidator createAddressValidator(String cryptoCurrency) {
        if (ICurrencies.SDC.equalsIgnoreCase(cryptoCurrency)) {
            return new ShadowcashAddressValidator();
        }
        return null;
    }

    @Override
    public IPaperWalletGenerator createPaperWalletGenerator(String cryptoCurrency) {
        if (ICurrencies.SDC.equalsIgnoreCase(cryptoCurrency)) {
            return new ShadowcashPaperWalletGenerator();
        }
        return null;
    }

    @Override
    public IRateSource createRateSource(String sourceLogin) {

        if (sourceLogin != null && !sourceLogin.trim().isEmpty()) {
            StringTokenizer st = new StringTokenizer(sourceLogin, ":");
            String rateSourceType = st.nextToken();
            String preferredFiatCurrency = ICurrencies.USD;

            if ("sdcfix".equalsIgnoreCase(rateSourceType)) {
                BigDecimal rate = BigDecimal.ZERO;
                if (st.hasMoreTokens()) {
                    try {
                        rate = new BigDecimal(st.nextToken());
                    } catch (Throwable e) {
                    }
                }
                if (st.hasMoreTokens()) {
                    preferredFiatCurrency = st.nextToken();
                }
                return new FixPriceRateSource(rate, preferredFiatCurrency);
            } else if ("sdcpoloniexrs".equalsIgnoreCase(rateSourceType)) {
                if (st.hasMoreTokens()) {
                    preferredFiatCurrency = st.nextToken();
                }
                return new PoloniexRateSource(preferredFiatCurrency);
            } else if ("sdcbittrexrs".equalsIgnoreCase(rateSourceType)) {
                if (st.hasMoreTokens()) {
                    preferredFiatCurrency = st.nextToken();
                }
                return new BittrexRateSource(preferredFiatCurrency);
            }

        }
        return null;
    }

    @Override
    public Set<String> getSupportedCryptoCurrencies() {
        Set<String> result = new HashSet<String>();
        result.add(ICurrencies.SDC);
        return result;
    }

    @Override
    public Set<String> getSupportedWatchListsNames() {
        return null;
    }

    @Override
    public IWatchList getWatchList(String name) {
        return null;
    }
}
