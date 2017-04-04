/*************************************************************************************
 * Copyright (C) 2014-2016 GENERAL BYTES s.r.o. All rights reserved.
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
package com.generalbytes.batm.server.extensions.extra.maxcoin;

import com.generalbytes.batm.server.extensions.*;
import com.generalbytes.batm.server.extensions.extra.maxcoin.sources.FixPriceRateSource;
import com.generalbytes.batm.server.extensions.extra.maxcoin.sources.MaxcoinTickerRateSource;
import com.generalbytes.batm.server.extensions.extra.maxcoin.wallets.maxcoind.MaxcoindRPCWallet;
import com.generalbytes.batm.server.extensions.watchlist.IWatchList;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

public class MaxcoinExtension implements IExtension{
    @Override
    public String getName() {
        return "BATM Maxcoin extension";
    }

    @Override
    public IExchange createExchange(String exchangeLogin) {
        return null;
    }

    @Override
    public IWallet createWallet(String walletLogin) {
        if (walletLogin !=null && !walletLogin.trim().isEmpty()) {
            StringTokenizer st = new StringTokenizer(walletLogin,":");
            String walletType = st.nextToken();

            if ("maxcoind".equalsIgnoreCase(walletType)) {
                //"maxcoind:protocol:user:password:ip:port:accountname"

                String protocol = st.nextToken();
                String username = st.nextToken();
                String password = st.nextToken();
                String hostname = st.nextToken();
                String port = st.nextToken();
                String accountName ="";
                if (st.hasMoreTokens()) {
                    accountName = st.nextToken();
                }


                if (protocol != null && username != null && password != null && hostname !=null && port != null && accountName != null) {
                    String rpcURL = protocol +"://" + username +":" + password + "@" + hostname +":" + port;
                    return new MaxcoindRPCWallet(rpcURL,accountName);
                }
            }
        }
        return null;
    }

    @Override
    public ICryptoAddressValidator createAddressValidator(String cryptoCurrency) {
        if (ICurrencies.MAX.equalsIgnoreCase(cryptoCurrency)) {
            return new MaxcoinAddressValidator();
        }
        return null;
    }

    @Override
    public IPaperWalletGenerator createPaperWalletGenerator(String cryptoCurrency) {
        return null;
    }

    @Override
    public IRateSource createRateSource(String sourceLogin) {
        if (sourceLogin != null && !sourceLogin.trim().isEmpty()) {
            StringTokenizer st = new StringTokenizer(sourceLogin,":");
            String exchangeType = st.nextToken();

            if ("maxcointicker".equalsIgnoreCase(exchangeType)) {
                return new MaxcoinTickerRateSource();
            }else if ("maxfix".equalsIgnoreCase(exchangeType)) {
                BigDecimal rate = BigDecimal.ZERO;
                if (st.hasMoreTokens()) {
                    try {
                        rate = new BigDecimal(st.nextToken());
                    } catch (Throwable e) {
                    }
                }
                String preferedFiatCurrency = ICurrencies.USD;
                if (st.hasMoreTokens()) {
                    preferedFiatCurrency = st.nextToken().toUpperCase();
                }
                return new FixPriceRateSource(rate,preferedFiatCurrency);
            }

        }
        return null;
    }

    @Override
    public IPaymentProcessor createPaymentProcessor(String paymentProcessorLogin) {
        return null; //no payment processors available
    }

    @Override
    public Set<String> getSupportedCryptoCurrencies() {
        Set<String> result = new HashSet<String>();
        result.add(ICurrencies.MAX);
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
