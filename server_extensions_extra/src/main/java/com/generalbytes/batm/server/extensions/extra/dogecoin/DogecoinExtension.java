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
package com.generalbytes.batm.server.extensions.extra.dogecoin;

import com.generalbytes.batm.server.extensions.*;
import com.generalbytes.batm.server.extensions.extra.dogecoin.sources.FixPriceRateSource;
import com.generalbytes.batm.server.extensions.extra.dogecoin.sources.chainso.ChainSoRateSource;
import com.generalbytes.batm.server.extensions.extra.dogecoin.wallets.blockio.BlockIOWallet;
import com.generalbytes.batm.server.extensions.extra.dogecoin.wallets.dogecoind.DogecoindRPCWallet;
import com.generalbytes.batm.server.extensions.watchlist.IWatchList;

import java.math.BigDecimal;
import java.util.*;

public class DogecoinExtension implements IExtension{
    @Override
    public String getName() {
        return "BATM Dogecoin extra extension";
    }

    @Override
    public IExchange createExchange(String exchangeLogin) {
        return null;
    }

    @Override
    public IPaymentProcessor createPaymentProcessor(String paymentProcessorLogin) {
        return null; //no payment processors available
    }

    @Override
    public IWallet createWallet(String walletLogin) {
        if (walletLogin !=null && !walletLogin.trim().isEmpty()) {
            StringTokenizer st = new StringTokenizer(walletLogin,":");
            String walletType = st.nextToken();

            if ("blockio".equalsIgnoreCase(walletType)) {
                String apikey = st.nextToken();
                String pin = st.nextToken();
                String priority = null;
                if (st.hasMoreTokens()) {
                    priority = st.nextToken();
                }
                return new BlockIOWallet(apikey,pin, priority);

            }else if ("dogecoind".equalsIgnoreCase(walletType)) {
                //"dogecoind:protocol:user:password:ip:port:accountname"

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
                    return new DogecoindRPCWallet(rpcURL,accountName);
                }
            }

        }
        return null;
    }

    @Override
    public ICryptoAddressValidator createAddressValidator(String cryptoCurrency) {
        if (ICurrencies.DOGE.equalsIgnoreCase(cryptoCurrency)) {
            return new DogecoinAddressValidator();
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

            if ("chainso".equalsIgnoreCase(exchangeType)) {
                return new ChainSoRateSource();
            }else if ("dogefix".equalsIgnoreCase(exchangeType)) {
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
        result.add(ICurrencies.DOGE);
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
