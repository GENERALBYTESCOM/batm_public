/*************************************************************************************
 * Copyright (C) 2014-2021 GENERAL BYTES s.r.o. All rights reserved.
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
package com.generalbytes.batm.server.extensions.extra.simplecoin;

import com.generalbytes.batm.server.extensions.AbstractExtension;
import com.generalbytes.batm.server.extensions.ExtensionsUtil;
import com.generalbytes.batm.server.extensions.IRateSource;
import com.generalbytes.batm.server.extensions.ExtensionsUtil;
import com.generalbytes.batm.server.extensions.extra.simplecoin.sources.SimpleCoinRateSource;
import com.generalbytes.batm.server.extensions.extra.simplecoin.sources.SupportedCurrencies;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.StringTokenizer;

public class SimpleCoinExtension extends AbstractExtension {
    private static final Logger log = LoggerFactory.getLogger(SimpleCoinExtension.class);
    private SupportedCurrencies supportedCurrencies;

    public SimpleCoinExtension() {
        supportedCurrencies = new SupportedCurrencies();
    }

    @Override
    public String getName() {
        return "SimpleCoin RateSource extension";
    }

    @Override
    public IRateSource createRateSource(String sourceLogin) {
        if (sourceLogin != null && !sourceLogin.trim().isEmpty()) {
            try {
                StringTokenizer st = new StringTokenizer(sourceLogin, ":");
                String rsType = st.nextToken();
                if ("simplecoin".equalsIgnoreCase(rsType)) {
                    if (st.hasMoreTokens()) {
                        supportedCurrencies.setPreferredFiatCurrency(st.nextToken());
                    }
                    return new SimpleCoinRateSource(supportedCurrencies);
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
        return supportedCurrencies.getSupportedCryptoCurrency();
    }
}