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
import com.generalbytes.batm.server.extensions.extra.lisk.wallets.liskbnb.Lskwallet;

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

            if ("liskbnb".equalsIgnoreCase(walletType)) {
                //"liskbnb:address:bnbkey:bnbsecret" 

                String address = st.nextToken();
                String bnbapikey = st.nextToken();
                String bnbapisecret = st.nextToken(); 

                if ( address != null && bnbapikey !=null && bnbapisecret != null ) {
                    return new Lskwallet(address,bnbapikey,bnbapisecret);
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
            if ("lisk_bnb".equalsIgnoreCase(exchangeType)) {
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
