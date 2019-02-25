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
package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.dvchain;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.common.currencies.FiatCurrency;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.XChangeExchange;
import org.knowm.xchange.ExchangeSpecification;

import java.util.HashSet;
import java.util.Set;

public class DVChainExchange extends XChangeExchange {


    public DVChainExchange(String apiSecret, String preferredFiatCurrency) {
        super(getSpecification(apiSecret), preferredFiatCurrency);
    }


    private static ExchangeSpecification getDefaultSpecification() {
        return new org.knowm.xchange.dvchain.DVChainExchange().getDefaultExchangeSpecification();
    }

    private static ExchangeSpecification getSpecification(String apiSecret) {
        ExchangeSpecification spec = getDefaultSpecification();
        spec.setSecretKey(apiSecret);
        return spec;
    }


    @Override
    protected double getAllowedCallsPerSecond() {
        return 10;
    }

    @Override
    protected boolean isWithdrawSuccessful(String string) {
        return true;
    }

    @Override
    public Set<String> getFiatCurrencies() {
        Set<String> fiatCurrencies = new HashSet<>();
        fiatCurrencies.add(FiatCurrency.USD.getCode());
        return fiatCurrencies;
    }

    @Override
    public Set<String> getCryptoCurrencies() {
        Set<String> cryptoCurrencies = new HashSet<>();
        cryptoCurrencies.add(CryptoCurrency.BTC.getCode());
        cryptoCurrencies.add(CryptoCurrency.BCH.getCode());
        cryptoCurrencies.add(CryptoCurrency.LTC.getCode());
        cryptoCurrencies.add(CryptoCurrency.ETH.getCode());
        cryptoCurrencies.add(CryptoCurrency.XMR.getCode());
        return cryptoCurrencies;
    }
}
