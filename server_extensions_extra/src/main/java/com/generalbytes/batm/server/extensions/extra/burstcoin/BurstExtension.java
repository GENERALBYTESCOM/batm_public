/*************************************************************************************
 * Copyright (C) 2015-2016 GENERAL BYTES s.r.o. All rights reserved.
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
 * GENERAL BYTES s.r.o
 * Web      :  http://www.generalbytes.com
 *
 ************************************************************************************/

package com.generalbytes.batm.server.extensions.extra.burstcoin;

import com.generalbytes.batm.server.extensions.*;
import com.generalbytes.batm.server.extensions.extra.burstcoin.sources.FixPriceRateSource;
import com.generalbytes.batm.server.extensions.extra.burstcoin.sources.poloniex.PoloniexRateSource;
import com.generalbytes.batm.server.extensions.extra.burstcoin.wallets.burstwallet.BurstWallet;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.StringTokenizer;

public class BurstExtension extends AbstractExtension{
    @Override
    public String getName() {
        return "BATM Burstcoin extension";
    }

    @Override
    public IExchange createExchange(String exchangeLogin) {
        return null;
    }

    @Override
    public IWallet createWallet(String walletLogin) {
        if (walletLogin != null && !walletLogin.trim().isEmpty()) {
            StringTokenizer st = new StringTokenizer(walletLogin,":");
            String walletType = st.nextToken();

            if ("burst".equalsIgnoreCase(walletType)) {
                String masterPassword = st.nextToken();
                String accountId = null;
                String nodeAddress = null;
                String nodePort = null;
                String useSSL = null;
                if (st.hasMoreTokens()) {
                    accountId = st.nextToken();
                }

                if (st.hasMoreTokens()) {
                    nodeAddress = st.nextToken();
                }

                if (st.hasMoreTokens()) {
                    useSSL = st.nextToken();
                }

                if (st.hasMoreTokens()) {
                    nodePort = st.nextToken();
                }

                if (masterPassword != null && accountId != null && nodeAddress != null) {
                    if (Objects.equals(useSSL, "true")) {
                        nodeAddress = "https://" + nodeAddress;
                    } else {
                        nodeAddress = "http://" + nodeAddress;
                    }

                    if (nodePort != null) {
                        nodeAddress += ":" + nodePort;
                    }

                    return new BurstWallet(masterPassword, accountId, nodeAddress);
                }
            }
        }
        return null;
    }

    @Override
    public ICryptoAddressValidator createAddressValidator(String cryptoCurrency) {
        if (Currencies.BURST.equalsIgnoreCase(cryptoCurrency)) {
            return new BurstAddressValidator();
        }
        return null;
    }

    @Override
    public IRateSource createRateSource(String sourceLogin) {
        if (sourceLogin != null && !sourceLogin.trim().isEmpty()) {
            StringTokenizer st = new StringTokenizer(sourceLogin,":");
            String rsType = st.nextToken();

            if ("burstfix".equalsIgnoreCase(rsType)) {
                BigDecimal rate = BigDecimal.ZERO;
                if (st.hasMoreTokens()) {
                    try {
                        rate = new BigDecimal(st.nextToken());
                    } catch (Throwable ignored) {}
                }
                String preferedFiatCurrency = Currencies.USD;
                if (st.hasMoreTokens()) {
                    preferedFiatCurrency = st.nextToken().toUpperCase();
                }
                return new FixPriceRateSource(rate,preferedFiatCurrency);
            } else if ("poloniexburst".equalsIgnoreCase(rsType)) {
                return new PoloniexRateSource();
            }
        }
        return null;
    }

    @Override
    public Set<String> getSupportedCryptoCurrencies() {
        Set<String> result = new HashSet<>();
        result.add(Currencies.BURST);
        return result;
    }
}
