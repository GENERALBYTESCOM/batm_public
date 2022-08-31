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
package com.generalbytes.batm.server.extensions.extra.betverseico;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.common.currencies.FiatCurrency;
import com.generalbytes.batm.server.extensions.*;
import com.generalbytes.batm.server.extensions.extra.betverseico.sources.fixed.BetVerseIcoFixedRateSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

public class BetVerseIcoExtension extends AbstractExtension {

    private static final Logger log = LoggerFactory.getLogger(BetVerseIcoExtension.class);

    private static final ICryptoCurrencyDefinition DEFINITION = new BetVerseIcoDefinition();

    @Override
    public String getName() {
        return "BATM BETVERSE ICO extension";
    }

    @Override
    public IWallet createWallet(String walletLogin, String tunnelPassword) {
        if (walletLogin != null && !walletLogin.trim().isEmpty()) {
            StringTokenizer st = new StringTokenizer(walletLogin, ":");
            String walletType = st.nextToken();

            if (!walletType.equals("betverseICO-wallet"))
                return null;

            Long chainID = Long.parseLong(st.nextToken());

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
                return new BetVerseIcoERC20Wallet(chainID, rpcURL,
                        passwordOrMnemonic, tokenSymbol, tokenDecimalPlaces,
                        contractAddress, gasLimit, gasPriceMultiplier);
            }

        }
        return null;
    }

    @Override
    public ICryptoAddressValidator createAddressValidator(String cryptoCurrency) {
        if (CryptoCurrency.BET_VERSE_ICO.getCode().equalsIgnoreCase(cryptoCurrency)) {
            return new BetVerseIcoAddressValidator();
        }
        return null;
    }

    @Override
    public IPaperWalletGenerator createPaperWalletGenerator(String cryptoCurrency) {
        if (CryptoCurrency.BET_VERSE_ICO.getCode().equalsIgnoreCase(cryptoCurrency)) {
            return new BetVerseIcoWalletGenerator(ctx);
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
            return new BetVerseIcoFixedRateSource(rate, preferedFiatCurrency);
        }
        return null;
    }

    @Override
    public Set<String> getSupportedCryptoCurrencies() {
        Set<String> result = new HashSet<String>();
        result.add(CryptoCurrency.BET_VERSE_ICO.getCode());
        return result;
    }
}
