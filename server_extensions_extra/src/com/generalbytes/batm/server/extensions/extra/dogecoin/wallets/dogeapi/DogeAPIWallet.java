/*************************************************************************************
 * Copyright (C) 2014 GENERAL BYTES s.r.o. All rights reserved.
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
package com.generalbytes.batm.server.extensions.extra.dogecoin.wallets.dogeapi;

import com.generalbytes.batm.server.extensions.ICurrencies;
import com.generalbytes.batm.server.extensions.IWallet;
import si.mazi.rescu.RestProxyFactory;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

public class DogeAPIWallet implements IWallet{
    private String apiKey;
    private IDogeAPIv2 api;
    private String pin;

    public DogeAPIWallet(String apiKey, String pin) {
        this.apiKey = apiKey;
        this.pin = pin;

        api = RestProxyFactory.createProxy(IDogeAPIv2.class, "https://www.dogeapi.com");
    }

    @Override
    public Set<String> getCryptoCurrencies() {
        Set<String> result = new HashSet<String>();
        result.add(ICurrencies.DOGE);
        return result;
    }

    @Override
    public String getPreferredCryptoCurrency() {
        return ICurrencies.DOGE;
    }

    @Override
    public String getCryptoAddress(String cryptoCurrency) {
        if (!cryptoCurrency.equalsIgnoreCase(ICurrencies.DOGE)) {
            return null;
        }
        DogeAPIResponse response = api.getAddresses(apiKey);

        if (response != null && response.getData() != null && response.getData().getAddresses() != null && response.getData().getAddresses().length > 0) {
            return response.getData().getAddresses()[0];
        }

        return null;
    }


    @Override
    public BigDecimal getCryptoBalance(String cryptoCurrency) {
        if (!cryptoCurrency.equalsIgnoreCase(ICurrencies.DOGE)) {
            return null;
        }
        DogeAPIResponse response = api.getBalance(apiKey);

        if (response != null && response.getData() != null && response.getData().getBalance() != null) {
            return response.getData().getBalance();
        }

        return null;
    }

    @Override
    public String sendCoins(String destinationAddress, BigDecimal amount, String cryptoCurrency) {
        if (!cryptoCurrency.equalsIgnoreCase(ICurrencies.DOGE)) {
            return null;
        }
        DogeAPIResponse withdraw = api.withdraw(apiKey, pin, amount, destinationAddress);
        if (withdraw != null && withdraw.getData() != null) {
            return withdraw.getData().getTxid();
        }

        return null;
    }
}
