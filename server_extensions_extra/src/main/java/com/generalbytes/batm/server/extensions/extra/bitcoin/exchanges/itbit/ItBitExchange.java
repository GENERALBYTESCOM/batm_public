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
import org.knowm.xchange.dto.account.AccountInfo;
import org.knowm.xchange.dto.account.Wallet;

import java.util.HashSet;
import java.util.Set;

public class ItBitExchange extends XChangeExchange {

    private String accountId;

    public ItBitExchange(String preferredFiatCurrency) {
        super(getDefaultSpecification(), preferredFiatCurrency);
    }

    public ItBitExchange(String userId, String accountId, String clientKey, String clientSecret, String preferredFiatCurrency) {
        super(getSpecification(userId, accountId, clientKey, clientSecret), preferredFiatCurrency);
        this.accountId = accountId;
    }


    private static ExchangeSpecification getDefaultSpecification() {
        return new org.knowm.xchange.itbit.v1.ItBitExchange().getDefaultExchangeSpecification();
    }

    private static ExchangeSpecification getSpecification(String userId, String accountId, String clientKey, String clientSecret) {
        ExchangeSpecification spec = getDefaultSpecification();
        spec.setExchangeSpecificParametersItem("userId", userId);
        spec.setExchangeSpecificParametersItem("walletId", accountId);
        spec.setApiKey(clientKey);
        spec.setSecretKey(clientSecret);
        return spec;
    }

    @Override
    public Set<String> getCryptoCurrencies() {
        Set<String> cryptoCurrencies = new HashSet<>();
        cryptoCurrencies.add(ICurrencies.BTC);
        return cryptoCurrencies;
    }

    @Override
    public Set<String> getFiatCurrencies() {
        Set<String> fiatCurrencies = new HashSet<>();
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
    public Wallet getWallet(AccountInfo accountInfo, String fiatCurrency) {
        return accountInfo.getWallet(accountId);
    }

    @Override
    protected String translateCryptoCurrencySymbolToExchangeSpecificSymbol(String from) {
        if (ICurrencies.BTC.equalsIgnoreCase(from)) {
            return "XBT";
        }
        return from;
    }
}
