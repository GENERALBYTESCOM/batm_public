/*************************************************************************************
 * Copyright (C) 2014-2019 GENERAL BYTES s.r.o. All rights reserved.
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
package com.generalbytes.batm.server.extensions.extra.aeternity;

import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.server.extensions.AbstractExtension;
import com.generalbytes.batm.server.extensions.ICryptoAddressValidator;
import com.generalbytes.batm.server.extensions.ICryptoCurrencyDefinition;
import com.generalbytes.batm.server.extensions.IRateSource;
import com.generalbytes.batm.server.extensions.IWallet;
import com.generalbytes.batm.server.extensions.extra.aeternity.coingecko.CoinGecko;
import com.kryptokrauts.aeternity.sdk.util.EncodingUtils;

public class AeternityExtension extends AbstractExtension {
    private static final ICryptoCurrencyDefinition DEFINITION = new AeternityDefinition();
    public static final String CURRENCY = CryptoCurrency.AE.getCode();

    @Override
    public String getName() {
        return "BATM Aeternity extra extension";
    }

    @Override
    public Set<String> getSupportedCryptoCurrencies() {
    	HashSet<String> result = new HashSet<>();
        result.add(CURRENCY);
        return result;
    }
    
    @Override
    public IWallet createWallet(String walletLogin) {
    	System.out.println("wallet login:" + walletLogin);       
        if (walletLogin !=null && !walletLogin.trim().isEmpty()) {
            StringTokenizer st = new StringTokenizer(walletLogin,":");
            String walletType = st.nextToken();
            System.out.println("walletType:" + walletType);
            if ("aeternity".equalsIgnoreCase(walletType)) {
                String mnemonic = st.hasMoreTokens() ?  st.nextToken() : null;
            	return new AeternityWallet(mnemonic);
            }
        }
        return null;
    }

    @Override
    public IRateSource createRateSource(String sourceLogin) {
    	System.out.println("sourceLogin: " + sourceLogin);
        if (sourceLogin != null && !sourceLogin.trim().isEmpty()) {
            if (sourceLogin.contains("coingecko")) {
                return new CoinGecko();
            }
        }
        return null;
    }

    @Override
    public ICryptoAddressValidator createAddressValidator(String cryptoCurrency) {
        if (!getSupportedCryptoCurrencies().contains(cryptoCurrency)) {
            return null;
        }
        return new ICryptoAddressValidator() {

            @Override
            public boolean isAddressValid(String address) {
                return EncodingUtils.isAddressValid(address);
            }

            @Override
            public boolean isPaperWalletSupported() {
                return true;
            }

            @Override
            public boolean mustBeBase58Address() {
                return false;
            }
        };
    }

    @Override
    public Set<ICryptoCurrencyDefinition> getCryptoCurrencyDefinitions() {
        Set<ICryptoCurrencyDefinition> result = new HashSet<>();
        result.add(DEFINITION);
        return result;
    }
}
