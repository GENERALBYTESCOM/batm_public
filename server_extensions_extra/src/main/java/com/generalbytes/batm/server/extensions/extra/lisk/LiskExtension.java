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
package com.generalbytes.batm.server.extensions.extra.lisk;

import com.generalbytes.batm.server.extensions.*;
import com.generalbytes.batm.server.extensions.extra.lisk.sources.FixPriceRateSource;
import com.generalbytes.batm.server.extensions.extra.lisk.sources.binance.BinanceRateSource;
import com.generalbytes.batm.server.extensions.extra.lisk.wallets.liskbinancewallet.LskWallet;

import java.math.BigDecimal;
import java.util.*;

public class LiskExtension extends AbstractExtension{
    @Override
    public String getName() {
        return "BATM Lisk extra extension";
    }

    @Override
    public IWallet createWallet(String walletLogin) {
        if (walletLogin !=null && !walletLogin.trim().isEmpty()) {
            StringTokenizer st = new StringTokenizer(walletLogin,":");
            String walletType = st.nextToken();

            if ("liskBinance".equalsIgnoreCase(walletType)) {
                //"liskBinance:address:binanceApiKey:binanceApiSecret" 

                String address = st.nextToken();
                String binanceApiKey = st.nextToken();
                String binanceApiSecret = st.nextToken(); 

                if (address != null && binanceApiKey !=null && binanceApiSecret != null ) {
                    return new LskWallet(address,binanceApiKey,binanceApiSecret);
                }
            }
        }
        return null;
    }

    @Override
    public ICryptoAddressValidator createAddressValidator(String cryptoCurrency) {
        if (Currencies.LSK.equalsIgnoreCase(cryptoCurrency)) {
            return new LiskAddressValidator();
        }
        return null;
    }

    @Override
    public IRateSource createRateSource(String sourceLogin) {
        if (sourceLogin != null && !sourceLogin.trim().isEmpty()) {
            StringTokenizer st = new StringTokenizer(sourceLogin, ":");
            String exchangeType = st.nextToken();
            if ("lskFix".equalsIgnoreCase(exchangeType)) {
                BigDecimal rate = BigDecimal.ZERO;
                if (st.hasMoreTokens()) {
                    try {
                        rate = new BigDecimal(st.nextToken());
                    } catch (Throwable e) {
                    }
                }
                String preferedFiatCurrency = Currencies.USD;
                if (st.hasMoreTokens()) {
                    preferedFiatCurrency = st.nextToken().toUpperCase();
                }
                return new FixPriceRateSource(rate, preferedFiatCurrency);
            }
            else if ("binanceRateSource".equalsIgnoreCase(exchangeType)) {
                String preferedFiatCurrency = Currencies.USD;
                if (st.hasMoreTokens()) {
                    preferedFiatCurrency = st.nextToken().toUpperCase();
                }
                return new BinanceRateSource(preferedFiatCurrency);
            }
        }
        return null;
    }
    
    @Override
    public Set<String> getSupportedCryptoCurrencies() {
        Set<String> result = new HashSet<String>();
        result.add(Currencies.LSK); 
        return result;
    }

}