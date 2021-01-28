/*************************************************************************************
 * Copyright (C) 2014-2021 GENERAL BYTES s.r.o. All rights reserved.
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
package com.generalbytes.batm.server.extensions.extra.nano.wallets.nano_node;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.server.extensions.IGeneratesNewDepositCryptoAddress;
import com.generalbytes.batm.server.extensions.IQueryableWallet;
import com.generalbytes.batm.server.extensions.IWallet;
import com.generalbytes.batm.server.extensions.extra.nano.wallets.NanoRPCClient;
import com.generalbytes.batm.server.extensions.payment.ReceivedAmount;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.oczadly.karl.jnano.model.NanoAmount;
import uk.oczadly.karl.jnano.rpc.exception.RpcException;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.util.HashSet;
import java.util.Set;
import java.io.IOException;

public class NanoRPCWallet implements IWallet, IQueryableWallet, IGeneratesNewDepositCryptoAddress {
    private static final Logger log = LoggerFactory.getLogger(NanoRPCWallet.class);
    private static final String CRYPTO_CURRENCY = CryptoCurrency.NANO.getCode();

    public NanoRPCWallet(String rpcURL, String walletId, String accountName) {
        this.rpcURL = rpcURL;
        this.walletId = walletId;
        this.accountName = accountName;
    }

    private String rpcURL;
    private String accountName;
    private String walletId;

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
            log.error("nano_node wallet error: unknown cryptocurrency.");
            return null;
        }

        log.info("nano_node sending coins from " + accountName + " to: " + destinationAddress + " " + amount);
        try {
            String result = getClient(rpcURL).sendFrom(walletId, accountName, destinationAddress, amount);
            log.debug("result = " + result);
            return result;
        } catch (RpcException e) {
            log.error("Error", e);
            return null;
        } catch (IOException e) {
            log.error("Error", e);
            return null;
        }
    }

    @Override
    public String getCryptoAddress(String cryptoCurrency) {
        if (!CRYPTO_CURRENCY.equalsIgnoreCase(cryptoCurrency)) {
            log.error("nano_node wallet error: unknown cryptocurrency.");
            return null;
        }

        return accountName;
    }

    @Override
    public BigDecimal getCryptoBalance(String cryptoCurrency) {
        if (!CRYPTO_CURRENCY.equalsIgnoreCase(cryptoCurrency)) {
            log.error("nano_node wallet error: unknown cryptocurrency: " + cryptoCurrency);
            return null;
        }
        try {
            return NanoAmount.valueOfRaw(getClient(rpcURL).getConfirmedBalance(accountName)).getAsNano();
        } catch (RpcException e) {
            log.error("Error", e);
            return null;
        } catch (IOException e) {
            log.error("Error", e);
            return null;
        }
    }

    @Override
    public ReceivedAmount getReceivedAmount(String address, String cryptoCurrency) {
        if (!CRYPTO_CURRENCY.equalsIgnoreCase(cryptoCurrency)) {
            log.error("nano_node wallet error: unknown cryptocurrency: " + cryptoCurrency);
            return null;
        }
        try {

            // First check for a confirmed balance
            BigInteger confirmedBalance = getClient(rpcURL).getConfirmedBalance(address);
            BigInteger confirmedPendingBalance = getClient(rpcURL).getConfirmedPendingBalance(address);
            if (confirmedBalance.add(confirmedPendingBalance).compareTo(new BigInteger("0")) == 1) {
                return new ReceivedAmount(
                        NanoAmount.valueOfRaw(confirmedBalance.add(confirmedPendingBalance)).getAsNano(), 1);
            }

            // Return everything with confirmation 0
            BigInteger balance = getClient(rpcURL).getBalance(address);
            BigInteger pendingBalance = getClient(rpcURL).getPendingBalance(address);
            return new ReceivedAmount(NanoAmount.valueOfRaw(balance.add(pendingBalance)).getAsNano(), 0);
        } catch (RpcException e) {
            log.error("Error", e);
            return null;
        } catch (IOException e) {
            log.error("Error", e);
            return null;
        }
    }

    @Override
    public String generateNewDepositCryptoAddress(String cryptoCurrency, String label) {
        if (!CRYPTO_CURRENCY.equalsIgnoreCase(cryptoCurrency)) {
            log.error("nano_node wallet error: unknown cryptocurrency: " + cryptoCurrency);
            return null;
        }
        try {
            return getClient(rpcURL).newAccount(walletId);
        } catch (RpcException e) {
            log.error("Error", e);
            return null;
        } catch (IOException e) {
            log.error("Error", e);
            return null;
        }
    }

    private NanoRPCClient getClient(String rpcURL) {
        try {
            return new NanoRPCClient(rpcURL);
        } catch (MalformedURLException e) {
            log.error("Error", e);
        }
        return null;
    }
}
