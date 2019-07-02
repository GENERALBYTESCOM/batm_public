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
package com.generalbytes.batm.server.extensions.extra.lightningbitcoin;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.server.extensions.AbstractExtension;
import com.generalbytes.batm.server.extensions.ICryptoAddressValidator;
import com.generalbytes.batm.server.extensions.ICryptoCurrencyDefinition;
import com.generalbytes.batm.server.extensions.IWallet;
import com.generalbytes.batm.server.extensions.extra.lightningbitcoin.wallets.eclair.EclairWallet;

import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

public class LightningBitcoinExtension extends AbstractExtension {
    private static final ICryptoCurrencyDefinition DEFINITION = new LightningBitcoinDefinition();

    @Override
    public String getName() {
        return "BATM Bitcoin Lightning extension";
    }

    @Override
    public IWallet createWallet(String walletLogin) {
        if (walletLogin != null && !walletLogin.trim().isEmpty()) {
            StringTokenizer st = new StringTokenizer(walletLogin, ":");
            String walletType = st.nextToken();

            if ("eclair".equalsIgnoreCase(walletType)) {
                String scheme = st.nextToken();
                String host = st.nextToken();
                String port = st.nextToken();
                String password = st.nextToken();
                if (scheme != null && host != null && port != null && password != null) {
                    return new EclairWallet(scheme, host, Integer.parseInt(port), password);
                }
            }
        }
        return null;
    }

    @Override
    public ICryptoAddressValidator createAddressValidator(String cryptoCurrency) {
        if (CryptoCurrency.LBTC.getCode().equalsIgnoreCase(cryptoCurrency)) {
            return new LightningBitcoinAddressValidator();
        }
        return null;
    }

    @Override
    public Set<String> getSupportedCryptoCurrencies() {
        Set<String> result = new HashSet<>();
        result.add(CryptoCurrency.LBTC.getCode());
        return result;
    }

    @Override
    public Set<ICryptoCurrencyDefinition> getCryptoCurrencyDefinitions() {
        Set<ICryptoCurrencyDefinition> result = new HashSet<>();
        result.add(DEFINITION);
        return result;
    }
}
