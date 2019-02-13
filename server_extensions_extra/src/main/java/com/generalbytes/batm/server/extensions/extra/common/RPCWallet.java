/*************************************************************************************
 * Copyright (C) 2014-2018 GENERAL BYTES s.r.o. All rights reserved.
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
package com.generalbytes.batm.server.extensions.extra.common;

import wf.bitcoin.javabitcoindrpcclient.BitcoinRPCException;
import com.generalbytes.batm.server.extensions.IWallet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RPCWallet implements IWallet, IRPCWallet{
    private static final Logger log = LoggerFactory.getLogger(RPCWallet.class);
    private String cryptoCurrency;

    private String accountName;
    private RPCClient client;


    public RPCWallet(String rpcURL, String accountName, String cryptoCurrency) {
        this.accountName = accountName;
        this.cryptoCurrency = cryptoCurrency;
        client = createClient(cryptoCurrency, rpcURL);
    }

    @Override
    public Set<String> getCryptoCurrencies() {
        Set<String> result = new HashSet<String>();
        result.add(cryptoCurrency);
        return result;
    }

    @Override
    public String getPreferredCryptoCurrency() {
        return cryptoCurrency;
    }

    @Override
    public String sendCoins(String destinationAddress, BigDecimal amount, String cryptoCurrency, String description) {
        if (!this.cryptoCurrency.equalsIgnoreCase(cryptoCurrency)) {
            log.error("RPCWallet wallet error: unknown cryptocurrency.");
            return null;
        }

        log.info("RPCWallet sending coins from " + accountName + " to: " + destinationAddress + " " + amount);
        try {
            String result = client.sendFrom(accountName, destinationAddress, amount);
            log.debug("result = " + result);
            return result;
        } catch (BitcoinRPCException e) {
            log.error("Error", e);
            return null;
        }
    }

    @Override
    public String getCryptoAddress(String cryptoCurrency) {
        if (!this.cryptoCurrency.equalsIgnoreCase(cryptoCurrency)) {
            log.error("RPCWallet wallet error: unknown cryptocurrency.");
            return null;
        }

        try {
            List<String> addressesByAccount = client.getAddressesByAccount(accountName);
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
        if (!this.cryptoCurrency.equalsIgnoreCase(cryptoCurrency)) {
            log.error("RPCWallet wallet error: unknown cryptocurrency: " + cryptoCurrency);
            return null;
        }
        try {
            return client.getBalance(accountName);
        } catch (BitcoinRPCException e) {
            log.error("Error", e);
            return null;
        }
    }

    private static RPCClient createClient(String cryptoCurrency, String rpcURL) {
        try {
            return new RPCClient(cryptoCurrency, rpcURL);
        } catch (MalformedURLException e) {
            log.error("Error", e);
        }
        return null;
    }

    public RPCClient getClient() {
        return client;
    }
}
