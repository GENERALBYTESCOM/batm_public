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

package com.generalbytes.batm.server.extensions.extra.tokencoin;

import com.generalbytes.batm.server.extensions.ICryptoAddressValidator;
import com.generalbytes.batm.server.extensions.ICurrencies;
import com.generalbytes.batm.server.extensions.IExchange;
import com.generalbytes.batm.server.extensions.IExtension;
import com.generalbytes.batm.server.extensions.IPaperWalletGenerator;
import com.generalbytes.batm.server.extensions.IPaymentProcessor;
import com.generalbytes.batm.server.extensions.IRateSource;
import com.generalbytes.batm.server.extensions.IWallet;
import com.generalbytes.batm.server.extensions.extra.tokencoin.sources.FixPriceRateSource;
import com.generalbytes.batm.server.extensions.extra.tokencoin.wallets.paperwallet.TokencoinPaperWalletGenerator;
import com.generalbytes.batm.server.extensions.extra.tokencoin.wallets.tokencoind.TokenWallet;
import com.generalbytes.batm.server.extensions.watchlist.IWatchList;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

public class TKNExtension implements IExtension{
    @Override
    public String getName() { return "BATM TKN extension"; }

    @Override
    public IExchange createExchange(String exchangeLogin) { return null; }

    @Override
    public IWallet createWallet(String walletLogin) {
        if (walletLogin != null && !walletLogin.trim().isEmpty()) {
            StringTokenizer st = new StringTokenizer(walletLogin,":");
            String walletType = st.nextToken();

            if ("tokencoind".equalsIgnoreCase(walletType)) {
                //"nud:protocol:user:password:ip:port:accountname"

                String host = st.nextToken();
                String portn = st.nextToken();
                String accountid = st.nextToken();
                int port = Integer.parseInt(portn);


                if (host != null && portn != null ) {
                    return new TokenWallet(host, port, accountid);
                }
            }
        }
        return null;
    }

    @Override
    public ICryptoAddressValidator createAddressValidator(String cryptoCurrency) {
        if (ICurrencies.TKN.equalsIgnoreCase(cryptoCurrency)) {
            return new TKNAddressValidator();
        }
        return null;
    }

    @Override
    public IPaperWalletGenerator createPaperWalletGenerator(String cryptoCurrency) {
        if (ICurrencies.TKN.equalsIgnoreCase(cryptoCurrency)) {
            return new TokencoinPaperWalletGenerator();
        }
        return null;
    }

    @Override
    public IRateSource createRateSource(String sourceLogin) {
        if (sourceLogin != null && !sourceLogin.trim().isEmpty()) {
            StringTokenizer st = new StringTokenizer(sourceLogin,":");
            String rsType = st.nextToken();

            if ("tknfix".equalsIgnoreCase(rsType)) { // fixed price
                BigDecimal rate = BigDecimal.ZERO;
                if (st.hasMoreTokens()) {
                    try {
                        rate = new BigDecimal(st.nextToken());
                    } catch (Throwable e) {
                    }
                }
                String preferedFiatCurrency = ICurrencies.EUR;
                if (st.hasMoreTokens()) {
                    preferedFiatCurrency = st.nextToken().toUpperCase();
                }
                return new FixPriceRateSource(rate,preferedFiatCurrency);
            }

        }
        return null;
    }

    @Override
    public IPaymentProcessor createPaymentProcessor(String paymentProcessorLogin) {
        return null; //no payment processors available
    }

    @Override
    public Set<String> getSupportedCryptoCurrencies() {
        Set<String> result = new HashSet<String>();
        result.add(ICurrencies.TKN);
        return result;
    }

    @Override
    public Set<String> getSupportedWatchListsNames() {
        return null;
    }

    @Override
    public IWatchList getWatchList(String name) {
        return null;
    }
}
