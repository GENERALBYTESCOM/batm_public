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
package com.generalbytes.batm.server.extensions.extra.slp;

import com.generalbytes.batm.common.currencies.SlpToken;
import com.generalbytes.batm.server.extensions.AbstractExtension;
import com.generalbytes.batm.server.extensions.ICryptoAddressValidator;
import com.generalbytes.batm.server.extensions.ICryptoCurrencyDefinition;
import com.generalbytes.batm.server.extensions.IWallet;
import com.generalbytes.batm.server.extensions.extra.bitcoincash.SlpAddressValidator;
import com.generalbytes.batm.server.extensions.extra.slp.wallets.electroncashslp.ElectronCashSlpWallet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

public class SlpExtension extends AbstractExtension {
    private static final Logger log = LoggerFactory.getLogger(SlpExtension.class);
    private static final Set<ICryptoCurrencyDefinition> SLP_DEFINITIONS = SlpToken.SLP_TOKENS.values().stream().map(SlpDefinition::new).collect(Collectors.toSet());

    @Override
    public String getName() {
        return "BATM SLP Tokens Extension";
    }

    @Override
    public IWallet createWallet(String walletLogin, String tunnelPassword) {
        try {
            if (walletLogin != null && !walletLogin.trim().isEmpty()) {
                StringTokenizer st = new StringTokenizer(walletLogin, ":");
                String walletType = st.nextToken();

                if (walletType.equalsIgnoreCase("electroncashslp")) {
                    String user = st.nextToken();
                    String password = st.nextToken();
                    String host = st.nextToken();
                    int port = Integer.parseInt(st.nextToken());
                    return new ElectronCashSlpWallet(user, password, host, port);
                }
            }
        } catch (Exception e) {
            log.warn("", e);
        }
        return null;
    }

    @Override
    public Set<String> getSupportedCryptoCurrencies() {
        return SlpToken.SLP_TOKENS.keySet();
    }

    @Override
    public Set<ICryptoCurrencyDefinition> getCryptoCurrencyDefinitions() {
        return SLP_DEFINITIONS;
    }

    @Override
    public ICryptoAddressValidator createAddressValidator(String cryptoCurrency) {
        if (getSupportedCryptoCurrencies().contains(cryptoCurrency)) {
            return new SlpAddressValidator();
        }
        return null;
    }

}
