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
package com.generalbytes.batm.server.extensions.extra.bitcoincash.wallets;

import com.azazar.bitcoin.jsonrpcclient.BitcoinException;
import com.azazar.bitcoin.jsonrpcclient.BitcoinJSONRPCClient;
import com.generalbytes.batm.server.extensions.IWallet;
import com.generalbytes.batm.server.extensions.extra.bitcoincash.RPCClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BATMBitcoinCashdRPCWallet implements IWallet{
    private static final Logger log = LoggerFactory.getLogger(BATMBitcoinCashdRPCWallet.class);
    private String cryptoCurrency;

    private String accountName;
    private BitcoinJSONRPCClient client;


    public BATMBitcoinCashdRPCWallet(String rpcURL, String accountName, String cryptoCurrency) {
        this.accountName = accountName;
        this.cryptoCurrency = cryptoCurrency;
        client = createClient(rpcURL);
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
            log.error("BitcoinCashd wallet error: unknown cryptocurrency.");
            return null;
        }

        log.info("BitcoinCashd sending coins from " + accountName + " to: " + destinationAddress + " " + amount);
        try {
            String result = client.sendFrom(accountName, destinationAddress, amount.doubleValue());
            log.debug("result = " + result);
            return result;
        } catch (BitcoinException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String getCryptoAddress(String cryptoCurrency) {
        if (!this.cryptoCurrency.equalsIgnoreCase(cryptoCurrency)) {
            log.error("BitcoinCashd wallet error: unknown cryptocurrency.");
            return null;
        }

        try {
            List<String> addressesByAccount = client.getAddressesByAccount(accountName);
            if (addressesByAccount == null || addressesByAccount.size() == 0) {
                return null;
            }else{
                return addressesByAccount.get(0);
            }
        } catch (BitcoinException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public BigDecimal getCryptoBalance(String cryptoCurrency) {
        if (!this.cryptoCurrency.equalsIgnoreCase(cryptoCurrency)) {
            log.error("BitcoinCashd wallet error: unknown cryptocurrency: " + cryptoCurrency);
            return null;
        }
        try {
            double balance = client.getBalance(accountName);
            return BigDecimal.valueOf(balance);
        } catch (BitcoinException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static RPCClient createClient(String rpcURL) {
        try {
            return new RPCClient(rpcURL);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
