/*************************************************************************************
 * Copyright (C) 2014-2017 GENERAL BYTES s.r.o. All rights reserved.
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
package com.generalbytes.batm.server.extensions.extra.viacoin;

import com.generalbytes.batm.server.extensions.*;
import com.generalbytes.batm.server.extensions.extra.viacoin.sources.FixPriceRateSource;
import com.generalbytes.batm.server.extensions.extra.viacoin.sources.poloniex.PoloniexRateSource;
import com.generalbytes.batm.server.extensions.extra.viacoin.wallets.viacoind.ViacoindRPCWallet;
import com.generalbytes.batm.server.extensions.watchlist.IWatchList;

import java.math.BigDecimal;
import java.util.*;

public class ViacoinExtension implements IExtension{
    @Override
    public String getName(){
        return "BATM Viacoin extension";
    }

    @Override
    public IExchange createExchange(String exchangeLogin){
        return null;
    }

    @Override
    public IWallet createWallet(String walletLogin){
        if(walletLogin != null && !walletLogin.trim().isEmpty()){
            StringTokenizer st = new StringTokenizer(walletLogin,":");
            String walletType = st.nextToken();

            if("viacoind".equalsIgnoreCase(walletType)){
                //"viacoind::protocol:user:password:ip:port:accountname"

                String protocol = st.nextToken();
                String username = st.nextToken();
                String password = st.nextToken();
                String hostname = st.nextToken();
                String port = st.nextToken();
                String accountname = "";
                if(st.hasMoreTokens()){
                    accountname = st.nextToken();
                }


                if (protocol != null && username != null && password != null && hostname !=null && port != null && accountname != null) {
                    String rpcURL = protocol +"://" + username +":" + password + "@" + hostname +":" + port;
                    return new ViacoindRPCWallet(rpcURL,accountname);
                }
            }
        }
        return null;
    }

    @Override
    public ICryptoAddressValidator createAddressValidator(String cryptoCurrency){
        if(ICurrencies.VIA.equalsIgnoreCase(cryptoCurrency)){
            return new ViacoinAddressValidator();
        }
        return null;
    }

    @Override
    public IPaperWalletGenerator createPaperWalletGenerator(String cryptoCurrency) {
        return null;
    }

    @Override
    public IPaymentProcessor createPaymentProcessor(String paymentProcessorLogin){
        return null;
    }
    
    @Override
    public IRateSource createRateSource(String sourceLogin) {
        if (sourceLogin != null && !sourceLogin.trim().isEmpty()) {
            StringTokenizer st = new StringTokenizer(sourceLogin,":");
            String rsType = st.nextToken();

            if ("viafix".equalsIgnoreCase(rsType)) {
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
            }else if ("poloniexrs".equalsIgnoreCase(rsType)) {
                String preferredFiatCurrency = ICurrencies.USD;
                if (st.hasMoreTokens()) {
                    preferredFiatCurrency = st.nextToken();
                }
                return new PoloniexRateSource(preferredFiatCurrency);
            }

        }
        return null;
    }

    @Override
    public Set<String> getSupportedCryptoCurrencies(){
        Set<String> result = new HashSet<String>();
        result.add(ICurrencies.VIA);
        return result;
    }

    @Override
    public Set<String> getSupportedWatchListsNames(){
        return null;
    }

    @Override
    public IWatchList getWatchList(String name){
        return null;
    }

}