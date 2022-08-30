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
package com.generalbytes.batm.server.extensions.extra.ico;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.common.currencies.FiatCurrency;
import com.generalbytes.batm.server.extensions.*;
import com.generalbytes.batm.server.extensions.extra.ico.sources.fixed.ICOFixedRateSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.tx.gas.DefaultGasProvider;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

public class ICOExtension extends AbstractExtension {

    private static final Logger log = LoggerFactory.getLogger(ICOExtension.class);

    private static final ICryptoCurrencyDefinition DEFINITION = new ICODefinition();

    @Override
    public String getName() {
        return "BATM BETVERSE ICO extension";
    }

    @Override
    public IWallet createWallet(String walletLogin, String tunnelPassword) {
        if (walletLogin != null && !walletLogin.trim().isEmpty()) {
            StringTokenizer st = new StringTokenizer(walletLogin, ":");
            String walletType = st.nextToken();

            Long chainID = Long.parseLong(st.nextToken());

            String tokenSymbol = st.nextToken();
            int tokenDecimalPlaces = Integer.parseInt(st.nextToken());
            String contractAddress = st.nextToken();
            String currencyAddress = st.nextToken();
            String tokenAddress = st.nextToken();

            String rpcURL = st.nextToken();
            String passwordOrMnemonic = st.nextToken();
            BigInteger gasLimit = DefaultGasProvider.GAS_LIMIT;
            if (st.hasMoreTokens()) {
                gasLimit = new BigInteger(st.nextToken());
            }

            BigDecimal gasPriceMultiplier = new BigDecimal(DefaultGasProvider.GAS_PRICE);

            if (rpcURL != null && passwordOrMnemonic != null) {
                return new ICOERC20Wallet(chainID, rpcURL,
                        passwordOrMnemonic, tokenSymbol, tokenDecimalPlaces,
                        contractAddress,currencyAddress, tokenAddress, gasLimit, gasPriceMultiplier);
            }

        }
        return null;
    }

    @Override
    public ICryptoAddressValidator createAddressValidator(String cryptoCurrency) {
        if (CryptoCurrency.ICO.getCode().equalsIgnoreCase(cryptoCurrency)) {
            return new ICOAddressValidator();
        }
        return null;
    }

    @Override
    public IPaperWalletGenerator createPaperWalletGenerator(String cryptoCurrency) {
        if (CryptoCurrency.ICO.getCode().equalsIgnoreCase(cryptoCurrency)) {
            return new ICOWalletGenerator(ctx);
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
            return new ICOFixedRateSource(rate, preferedFiatCurrency);
        }
        return null;
    }

    @Override
    public Set<String> getSupportedCryptoCurrencies() {
        Set<String> result = new HashSet<String>();
        result.add(CryptoCurrency.ICO.getCode());
        return result;
    }
}
