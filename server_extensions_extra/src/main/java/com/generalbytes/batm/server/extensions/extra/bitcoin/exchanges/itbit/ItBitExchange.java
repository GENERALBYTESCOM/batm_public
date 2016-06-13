/*************************************************************************************
 * Copyright (C) 2015-2016 GENERAL BYTES s.r.o. All rights reserved.
 * <p/>
 * This software may be distributed and modified under the terms of the GNU
 * General Public License version 2 (GPL2) as published by the Free Software
 * Foundation and appearing in the file GPL2.TXT included in the packaging of
 * this file. Please note that GPL2 Section 2[b] requires that all works based
 * on this software must also be made publicly available under the terms of
 * the GPL2 ("Copyleft").
 * <p/>
 * Contact information
 * -------------------
 * <p/>
 * GENERAL BYTES s.r.o.
 * Web      :  http://www.generalbytes.com
 * <p/>
 * Other information:
 * <p/>
 * This implementation was created in cooperation with Sumbits http://www.getsumbits.com/
 ************************************************************************************/
package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.itbit;

import com.generalbytes.batm.server.extensions.ICurrencies;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.XChangeExchange;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.currency.Currency;

import java.util.HashSet;
import java.util.Set;

public class ItBitExchange extends XChangeExchange {

    public ItBitExchange(String preferredFiatCurrencyCode) {
        super(getDefaultSpecification(), Currency.getInstance(preferredFiatCurrencyCode));
    }

    public ItBitExchange(String userId, String walletId, String clientKey, String clientSecret, String preferredFiatCurrencyCode) {
        super(getSpecification(userId, walletId, clientKey, clientSecret), Currency.getInstance(preferredFiatCurrencyCode));
    }


    private static ExchangeSpecification getDefaultSpecification() {
        return new org.knowm.xchange.itbit.v1.ItBitExchange().getDefaultExchangeSpecification();
    }

    private static ExchangeSpecification getSpecification(String userId, String walletId, String clientKey, String clientSecret) {
        ExchangeSpecification spec = getDefaultSpecification();
        spec.setExchangeSpecificParametersItem("userId", userId);
        spec.setExchangeSpecificParametersItem("walletId", walletId);
        spec.setApiKey(clientKey);
        spec.setSecretKey(clientSecret);
        return spec;
    }

    @Override
    public Set<String> getCryptoCurrencies() {
        Set<String> cryptoCurrencies = new HashSet<String>();
        cryptoCurrencies.add(ICurrencies.BTC);
        return cryptoCurrencies;
    }

    @Override
    public Set<String> getFiatCurrencies() {
        Set<String> fiatCurrencies = new HashSet<String>();
        fiatCurrencies.add(ICurrencies.USD);
        fiatCurrencies.add(ICurrencies.EUR);
        fiatCurrencies.add(ICurrencies.SGD);
        return fiatCurrencies;
    }

    @Override
    protected boolean isWithdrawSuccessful(String result) {
        return true;
    }

    @Override
    protected double getAllowedCallsPerSecond() {
        return 10;
    }

    @Override
    protected String translateCryptoCurrencySymbolToExchangeSpecificSymbol(String from) {
        if (ICurrencies.BTC.equalsIgnoreCase(from)) {
            return "XBT";
        }
        return from;
    }
}