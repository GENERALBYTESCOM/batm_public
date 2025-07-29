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
package com.generalbytes.batm.server.extensions.extra.ripple;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.common.currencies.FiatCurrency;
import com.generalbytes.batm.server.extensions.AbstractExtension;
import com.generalbytes.batm.server.extensions.DummyExchangeAndWalletAndSource;
import com.generalbytes.batm.server.extensions.ExtensionsUtil;
import com.generalbytes.batm.server.extensions.FixPriceRateSource;
import com.generalbytes.batm.server.extensions.ICryptoAddressValidator;
import com.generalbytes.batm.server.extensions.IExchange;
import com.generalbytes.batm.server.extensions.IPaperWalletGenerator;
import com.generalbytes.batm.server.extensions.IRateSource;
import com.generalbytes.batm.server.extensions.IWallet;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

public class RippleExtension extends AbstractExtension {

    @Override
    public String getName() {
        return "BATM Ripple extension";
    }

    @Override
    public IWallet createWallet(String walletLogin, String tunnelPassword) {
        if (walletLogin != null && !walletLogin.trim().isEmpty()) {
            try {
                StringTokenizer st = new StringTokenizer(walletLogin, ":");
                String walletType = st.nextToken();

                if ("xrpdemo".equalsIgnoreCase(walletType)) {
                    return getDummyExchangeAndWallet(st);
                }
            } catch (Exception e) {
                ExtensionsUtil.logExtensionParamsException("createWallet", getClass().getSimpleName(), walletLogin, e);
            }
        }
        return null;
    }

    @Override
    public IExchange createExchange(String exchangeLogin) {
        if (exchangeLogin == null || exchangeLogin.isBlank()) {
            return null;
        }

        try {
            StringTokenizer st = new StringTokenizer(exchangeLogin, ":");
            String exchangeType = st.nextToken();

            if ("xrpdemo".equalsIgnoreCase(exchangeType)) {
                return getDummyExchangeAndWallet(st);
            }
        } catch (Exception e) {
            ExtensionsUtil.logExtensionParamsException("createExtension", getClass().getSimpleName(), exchangeLogin, e);
        }
        return null;
    }

    private DummyExchangeAndWalletAndSource getDummyExchangeAndWallet(StringTokenizer st) {
        String fiatCurrency = st.nextToken();
        String walletAddress;
        if (st.hasMoreTokens()) {
            walletAddress = st.nextToken();
        } else {
            walletAddress = "";
        }

        return new DummyExchangeAndWalletAndSource(fiatCurrency, CryptoCurrency.XRP.getCode(), walletAddress);
    }

    @Override
    public ICryptoAddressValidator createAddressValidator(String cryptoCurrency) {
        if (CryptoCurrency.XRP.getCode().equalsIgnoreCase(cryptoCurrency)) {
            return new RippleAddressValidator();
        }
        return null;
    }

    @Override
    public IPaperWalletGenerator createPaperWalletGenerator(String cryptoCurrency) {
        if (CryptoCurrency.XRP.getCode().equals(cryptoCurrency)) {
            return new RippleWalletGenerator(ctx);
        }
        return null;
    }

    @Override
    public IRateSource createRateSource(String sourceLogin) {
        if (sourceLogin != null && !sourceLogin.trim().isEmpty()) {
            try {
                StringTokenizer st = new StringTokenizer(sourceLogin, ":");
                String rsType = st.nextToken();

                if ("xrpfix".equalsIgnoreCase(rsType)) {
                    BigDecimal rate = BigDecimal.ZERO;
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
                    return new FixPriceRateSource(rate, preferedFiatCurrency);
                }
            } catch (Exception e) {
                ExtensionsUtil.logExtensionParamsException("createRateSource", getClass().getSimpleName(), sourceLogin, e);
            }

        }
        return null;
    }

    @Override
    public Set<String> getSupportedCryptoCurrencies() {
        Set<String> result = new HashSet<>();
        result.add(CryptoCurrency.XRP.getCode());
        return result;
    }
}
