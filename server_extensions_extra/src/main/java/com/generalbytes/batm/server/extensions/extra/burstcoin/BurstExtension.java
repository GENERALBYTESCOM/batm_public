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

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.common.currencies.FiatCurrency;
import com.generalbytes.batm.server.extensions.*;
import com.generalbytes.batm.server.extensions.FixPriceRateSource;
import com.generalbytes.batm.server.extensions.ExtensionsUtil;
import com.generalbytes.batm.server.extensions.extra.burstcoin.sources.poloniex.PoloniexRateSource;
import com.generalbytes.batm.server.extensions.extra.burstcoin.wallets.burstwallet.BurstWallet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.StringTokenizer;

public class BurstExtension extends AbstractExtension{

    private static final Logger log = LoggerFactory.getLogger(BurstExtension.class);

    @Override
    public String getName() {
        return "BATM Burstcoin extension";
    }

    @Override
    public IExchange createExchange(String exchangeLogin) {
        return null;
    }

    @Override
    public IWallet createWallet(String walletLogin, String tunnelPassword) {
        if (walletLogin != null && !walletLogin.trim().isEmpty()) {
            try {
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
                if ("burstdemo".equalsIgnoreCase(walletType)) {

                    String fiatCurrency = st.nextToken();
                    String walletAddress = "";
                    if (st.hasMoreTokens()) {
                        walletAddress = st.nextToken();
                    }

                    if (fiatCurrency != null && walletAddress != null) {
                        return new DummyExchangeAndWalletAndSource(fiatCurrency, CryptoCurrency.BURST.getCode(), walletAddress);
                    }
                }
            } catch (Exception e) {
                log.warn("createWallet failed for prefix: {}, {}: {} ",
                    ExtensionsUtil.getPrefixWithCountOfParameters(walletLogin), e.getClass().getSimpleName(), e.getMessage()
                );
            }
        }
        return null;
    }

    @Override
    public ICryptoAddressValidator createAddressValidator(String cryptoCurrency) {
        if (CryptoCurrency.BURST.getCode().equalsIgnoreCase(cryptoCurrency)) {
            return new BurstAddressValidator();
        }
        return null;
    }

    @Override
    public IRateSource createRateSource(String sourceLogin) {
        if (sourceLogin != null && !sourceLogin.trim().isEmpty()) {
            try {
                StringTokenizer st = new StringTokenizer(sourceLogin, ":");
                String rsType = st.nextToken();

                if ("burstfix".equalsIgnoreCase(rsType)) {
                    BigDecimal rate = BigDecimal.ZERO;
                    if (st.hasMoreTokens()) {
                        try {
                            rate = new BigDecimal(st.nextToken());
                        } catch (Throwable ignored) {
                        }
                    }
                    String preferedFiatCurrency = FiatCurrency.USD.getCode();
                    if (st.hasMoreTokens()) {
                        preferedFiatCurrency = st.nextToken().toUpperCase();
                    }
                    return new FixPriceRateSource(rate, preferedFiatCurrency);
                } else if ("poloniexburst".equalsIgnoreCase(rsType)) {
                    return new PoloniexRateSource();
                }
            } catch (Exception e) {
                log.warn("createRateSource failed for prefix: {}, {}: {} ",
                    ExtensionsUtil.getPrefixWithCountOfParameters(sourceLogin), e.getClass().getSimpleName(), e.getMessage()
                );
            }
        }
        return null;
    }

    @Override
    public Set<String> getSupportedCryptoCurrencies() {
        Set<String> result = new HashSet<>();
        result.add(CryptoCurrency.BURST.getCode());
        return result;
    }
}
