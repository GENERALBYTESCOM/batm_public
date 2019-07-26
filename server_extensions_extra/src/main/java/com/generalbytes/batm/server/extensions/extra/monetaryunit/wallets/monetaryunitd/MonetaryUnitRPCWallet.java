/*************************************************************************************
 * Copyright (C) 2014-2019 GENERAL BYTES s.r.o. All rights reserved.
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
package com.generalbytes.batm.server.extensions.extra.monetaryunit.wallets.monetaryunitd;

import wf.bitcoin.javabitcoindrpcclient.BitcoinRPCException;
import wf.bitcoin.javabitcoindrpcclient.BitcoinJSONRPCClient;
import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.server.extensions.IWallet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MonetaryUnitRPCWallet implements IWallet{
    private static final Logger log = LoggerFactory.getLogger(MonetaryUnitRPCWallet.class);
    private static final String CRYPTO_CURRENCY = CryptoCurrency.MUE.getCode();

    public MonetaryUnitRPCWallet(String rpcURL, String accountName) {
        this.rpcURL = rpcURL;
        this.accountName = accountName;
    }

    private String rpcURL;
    private String accountName;

    @Override
    public Set<String> getCryptoCurrencies() {
        Set<String> result = new HashSet<String>();
        result.add(CRYPTO_CURRENCY);
        return result;

    }

    @Override
    public String getPreferredCryptoCurrency() {
        return CRYPTO_CURRENCY;
    }

    @Override
    public String sendCoins(String destinationAddress, BigDecimal amount, String cryptoCurrency, String description) {
        if (!CRYPTO_CURRENCY.equalsIgnoreCase(cryptoCurrency)) {
            log.error("monetaryunitd wallet error: unknown cryptocurrency.");
            return null;
        }

        log.info("monetaryunitd sending coins from " + accountName + " to: " + destinationAddress + " " + amount);
        try {
            String result = getClient(rpcURL).sendFrom(accountName, destinationAddress, amount);
            log.debug("result = " + result);
            return result;
        } catch (BitcoinRPCException e) {
            log.error("Error", e);
            return null;
        }
    }

    @Override
    public String getCryptoAddress(String cryptoCurrency) {
        if (!CRYPTO_CURRENCY.equalsIgnoreCase(cryptoCurrency)) {
            log.error("monetaryunitd wallet error: unknown cryptocurrency.");
            return null;
        }

        try {
            List<String> addressesByAccount = getClient(rpcURL).getAddressesByAccount(accountName);
            if (addressesByAccount == null || addressesByAccount.size() == 0) {
                return null;
            }else{
                return addressesByAccount.get(0);
            }
        } catch (BitcoinRPCException e) {
            log.error("Error", e);
            return null;
        }
    }

    @Override
    public BigDecimal getCryptoBalance(String cryptoCurrency) {
        if (!CRYPTO_CURRENCY.equalsIgnoreCase(cryptoCurrency)) {
            log.error("monetaryunitd wallet error: unknown cryptocurrency: " + cryptoCurrency);
            return null;
        }
        try {
            return getClient(rpcURL).getBalance(accountName);
        } catch (BitcoinRPCException e) {
            log.error("Error", e);
            return null;
        }
    }

    private BitcoinJSONRPCClient getClient(String rpcURL) {
        try {
            return new BitcoinJSONRPCClient(rpcURL);
        } catch (MalformedURLException e) {
            log.error("Error", e);
        }
        return null;
    }
}
