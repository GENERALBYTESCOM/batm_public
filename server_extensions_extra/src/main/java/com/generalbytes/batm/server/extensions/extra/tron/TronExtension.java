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
package com.generalbytes.batm.server.extensions.extra.tron;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.server.extensions.AbstractExtension;
import com.generalbytes.batm.server.extensions.DummyExchangeAndWalletAndSource;
import com.generalbytes.batm.server.extensions.ICryptoAddressValidator;
import com.generalbytes.batm.server.extensions.IWallet;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.StringTokenizer;

public class TronExtension extends AbstractExtension {
    private static final Collection<String> supportedCryptoCurrencies = Collections.unmodifiableSet(new HashSet<String>() {{
        add(CryptoCurrency.TRX.getCode());
        add(CryptoCurrency.USDTTRON.getCode());
    }});


    @Override
    public String getName() {
        return "BATM Tron extension";
    }

    @Override
    public IWallet createWallet(String walletLogin, String tunnelPassword) {
        if (walletLogin != null && !walletLogin.trim().isEmpty()) {
            StringTokenizer st = new StringTokenizer(walletLogin, ":");
            String walletType = st.nextToken();

            if (walletType.startsWith("tridentTRC20_")) {
                StringTokenizer wt = new StringTokenizer(walletType, "_");
                wt.nextToken();
                String tokenSymbol = wt.nextToken();
                int tokenDecimalPlaces = Integer.parseInt(wt.nextToken());
                String contractAddress = wt.nextToken();

                String tronProApiKey = st.nextToken();
                String hexPrivateKey = st.nextToken();
                return new TRC20Wallet(tronProApiKey, hexPrivateKey, tokenSymbol, tokenDecimalPlaces, contractAddress);
            } else if ("usdttrondemo".equalsIgnoreCase(walletType)) {
                String fiatCurrency = st.nextToken();
                String walletAddress = "";
                if (st.hasMoreTokens()) {
                    walletAddress = st.nextToken();
                }
                if (fiatCurrency != null && walletAddress != null) {
                    return new DummyExchangeAndWalletAndSource(fiatCurrency, CryptoCurrency.USDTTRON.getCode(), walletAddress);
                }
            }
        }
        return null;
    }


    @Override
    public ICryptoAddressValidator createAddressValidator(String cryptoCurrency) {
        if (!supportedCryptoCurrencies.contains(cryptoCurrency)) {
            return null;
        }

        return new TronCryptoAddressValidator();
    }

}
