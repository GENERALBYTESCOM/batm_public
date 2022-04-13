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
package com.generalbytes.batm.server.extensions.extra.btoken;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.common.currencies.FiatCurrency;
import com.generalbytes.batm.server.extensions.*;
import com.generalbytes.batm.server.extensions.extra.btoken.sources.fixed.BTokenFixedRateSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

public class BTokenExtension extends AbstractExtension {

    private static final Logger log = LoggerFactory.getLogger(BTokenExtension.class);

    private static final ICryptoCurrencyDefinition DEFINITION = new BTokenDefinition();

    @Override
    public String getName() {
        return "BATM BTOKEN extension";
    }

    @Override
    public IWallet createWallet(String walletLogin, String tunnelPassword) {
        walletLogin = "btoken-wallet:BTOKEN:18:0x8a79dD9A76f6C8ffF22791c758A137213D54fD12:ropsten.infura.io/v3/fe61370c71034c7fadb5161f6a4381b9:liquid brand gaze spare someone toe cause nuclear rug west wash mask";
        if (walletLogin != null && !walletLogin.trim().isEmpty()) {
            StringTokenizer st = new StringTokenizer(walletLogin, ":");
            String walletType = st.nextToken(); // btoken-wallet

            String tokenSymbol = st.nextToken();
            int tokenDecimalPlaces = Integer.parseInt(st.nextToken());
            String contractAddress = st.nextToken();

            String rpcURL = st.nextToken();
            String passwordOrMnemonic = st.nextToken();
            BigInteger gasLimit = null;
            if (st.hasMoreTokens()) {
                gasLimit = new BigInteger(st.nextToken());
            }
            BigDecimal gasPriceMultiplier = BigDecimal.ONE;
            if (st.hasMoreTokens()) {
                gasPriceMultiplier = new BigDecimal(st.nextToken());
            }

            if (rpcURL != null && passwordOrMnemonic != null) {
                return new BTokenERC20Wallet(rpcURL,
                        passwordOrMnemonic, tokenSymbol, tokenDecimalPlaces,
                        contractAddress, gasLimit, gasPriceMultiplier);
            }

        }
        return null;
    }
/*
    @Override
    public Set<ICryptoCurrencyDefinition> getCryptoCurrencyDefinitions() {
        Set<ICryptoCurrencyDefinition> result = new HashSet<>();
        result.add(DEFINITION);
        return result;
    }
*/
    @Override
    public ICryptoAddressValidator createAddressValidator(String cryptoCurrency) {
        if (CryptoCurrency.BTOKEN.getCode().equalsIgnoreCase(cryptoCurrency)) {
            return new BTokenAddressValidator();
        }
        return null;
    }

    @Override
    public IPaperWalletGenerator createPaperWalletGenerator(String cryptoCurrency) {
        if (CryptoCurrency.BTOKEN.getCode().equalsIgnoreCase(cryptoCurrency)) {
            return new BTokenWalletGenerator(ctx);
        }
        return null;
    }

    @Override
    public IRateSource createRateSource(String sourceLogin) {
        if (sourceLogin != null && !sourceLogin.trim().isEmpty()) {
            StringTokenizer st = new StringTokenizer(sourceLogin, ":");
            String exchangeType = st.nextToken();
            BigDecimal rate = new BigDecimal(1);
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
            return new BTokenFixedRateSource(rate, preferedFiatCurrency);
        }
        return null;
    }

    @Override
    public Set<String> getSupportedCryptoCurrencies() {
        Set<String> result = new HashSet<String>();
        result.add(CryptoCurrency.BTOKEN.getCode());
        return result;
    }
}
