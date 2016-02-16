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

package com.generalbytes.batm.server.extensions.extra.watchlists;

import com.generalbytes.batm.server.extensions.*;
import com.generalbytes.batm.server.extensions.extra.watchlists.ofac.OFACWatchList;
import com.generalbytes.batm.server.extensions.watchlist.IWatchList;

import java.util.HashSet;
import java.util.Set;

public class BasicWatchlistsExtension implements IExtension{
    private IWatchList ofacWatchList = new OFACWatchList();

    @Override
    public String getName() {
        return "BasicWatchlistsExtension";
    }

    @Override
    public Set<String> getSupportedWatchListsNames() {
        final HashSet<String> watchListNames = new HashSet<String>();
        watchListNames.add(ofacWatchList.getName());
        return watchListNames;
    }

    @Override
    public IWatchList getWatchList(String name) {
        if (ofacWatchList.getName().equals(name)) {
            return ofacWatchList;
        }
        return null;
    }


    @Override
    public Set<String> getSupportedCryptoCurrencies() {
        return null;
    }

    @Override
    public IExchange createExchange(String exchangeLogin) {
        return null;
    }

    @Override
    public IPaymentProcessor createPaymentProcessor(String paymentProcessorLogin) {
        return null;
    }

    @Override
    public IRateSource createRateSource(String sourceLogin) {
        return null;
    }

    @Override
    public IWallet createWallet(String walletLogin) {
        return null;
    }

    @Override
    public ICryptoAddressValidator createAddressValidator(String cryptoCurrency) {
        return null;
    }

    @Override
    public IPaperWalletGenerator createPaperWalletGenerator(String cryptoCurrency) {
        return null;
    }

}
