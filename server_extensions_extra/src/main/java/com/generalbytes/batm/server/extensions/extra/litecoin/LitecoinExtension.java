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
package com.generalbytes.batm.server.extensions.extra.litecoin;

import com.generalbytes.batm.server.extensions.*;
import com.generalbytes.batm.server.extensions.extra.litecoin.sources.FixPriceRateSource;
import com.generalbytes.batm.server.extensions.extra.litecoin.sources.btce.BTCeRateSource;
import com.generalbytes.batm.server.extensions.extra.litecoin.wallets.litecoind.LitecoindRPCWallet;
import com.generalbytes.batm.server.extensions.watchlist.IWatchList;

import java.math.BigDecimal;
import java.util.*;

public class LitecoinExtension implements IExtension{
    @Override
    public String getName() {
        return "BATM Litecoin extension";
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

            if ("litecoind".equalsIgnoreCase(walletType)) {
                //"litecoind:protocol:user:password:ip:port:accountname"

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
                    return new LitecoindRPCWallet(rpcURL,accountName);
                }
            }
        }
        return null;
    }

    @Override
    public ICryptoAddressValidator createAddressValidator(String cryptoCurrency) {
        if (ICurrencies.LTC.equalsIgnoreCase(cryptoCurrency)) {
            return new LitecoinAddressValidator();
        }
        return null;
    }

    @Override
    public IPaperWalletGenerator createPaperWalletGenerator(String cryptoCurrency) {
        return null;
    }

    @Override
    public IPaymentProcessor createPaymentProcessor(String paymentProcessorLogin) {
        return null; //no payment processors available
    }

    @Override
    public IRateSource createRateSource(String sourceLogin) {
        if (sourceLogin != null && !sourceLogin.trim().isEmpty()) {
            StringTokenizer st = new StringTokenizer(sourceLogin,":");
            String exchangeType = st.nextToken();

            if ("btce".equalsIgnoreCase(exchangeType)) {
                if (st.hasMoreTokens()) {
                    return new BTCeRateSource(st.nextToken());
                }
                return new BTCeRateSource();
            }else if ("ltcfix".equalsIgnoreCase(exchangeType)) {
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
    public Set<String> getSupportedCryptoCurrencies() {
        Set<String> result = new HashSet<String>();
        result.add(ICurrencies.LTC);
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
