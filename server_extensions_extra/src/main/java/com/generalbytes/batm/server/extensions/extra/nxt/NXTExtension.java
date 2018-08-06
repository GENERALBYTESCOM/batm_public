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

package com.generalbytes.batm.server.extensions.extra.nxt;

import com.generalbytes.batm.server.extensions.*;
import com.generalbytes.batm.server.extensions.extra.nxt.sources.FixPriceRateSource;
import com.generalbytes.batm.server.extensions.extra.nxt.sources.poloniex.PoloniexRateSource;
import com.generalbytes.batm.server.extensions.extra.nxt.wallets.mynxt.MynxtWallet;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

public class NXTExtension extends AbstractExtension{
    @Override
    public String getName() { return "BATM NXT extension"; }

    @Override
    public IWallet createWallet(String walletLogin) {
        if (walletLogin != null && !walletLogin.trim().isEmpty()) {
            StringTokenizer st = new StringTokenizer(walletLogin,":");
            String walletType = st.nextToken();

            if ("mynxt".equalsIgnoreCase(walletType)) {
                //"nud:protocol:user:password:ip:port:accountname"

                String email = st.nextToken();
                String password = st.nextToken();
                String masterPassword = st.nextToken();
                String accountId =null;
                if (st.hasMoreTokens()) {
                    accountId = st.nextToken();
                }


                if (email != null && password != null && masterPassword !=null) {
                    return new MynxtWallet(email,password,masterPassword,accountId);
                }
            }
        }
        return null;
    }

    @Override
    public ICryptoAddressValidator createAddressValidator(String cryptoCurrency) {
        if (Currencies.NXT.equalsIgnoreCase(cryptoCurrency)) {
            return new NXTAddressValidator();
        }
        return null;
    }

    @Override
    public IRateSource createRateSource(String sourceLogin) {
        if (sourceLogin != null && !sourceLogin.trim().isEmpty()) {
            StringTokenizer st = new StringTokenizer(sourceLogin,":");
            String rsType = st.nextToken();

            if ("nxtfix".equalsIgnoreCase(rsType)) {
                BigDecimal rate = BigDecimal.ZERO;
                if (st.hasMoreTokens()) {
                    try {
                        rate = new BigDecimal(st.nextToken());
                    } catch (Throwable e) {
                    }
                }
                String preferedFiatCurrency = Currencies.USD;
                if (st.hasMoreTokens()) {
                    preferedFiatCurrency = st.nextToken().toUpperCase();
                }
                return new FixPriceRateSource(rate,preferedFiatCurrency);
            }else if ("poloniexrs".equalsIgnoreCase(rsType)) {
                String preferredFiatCurrency = Currencies.USD;
                if (st.hasMoreTokens()) {
                    preferredFiatCurrency = st.nextToken();
                }
                return new PoloniexRateSource(preferredFiatCurrency);
            }

        }
        return null;
    }

    @Override
    public Set<String> getSupportedCryptoCurrencies() {
        Set<String> result = new HashSet<String>();
        result.add(Currencies.NXT);
        return result;
    }
}
