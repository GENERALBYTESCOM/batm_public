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

package com.generalbytes.batm.server.extensions.extra.burstcoin.wallets.burstwallet;

import com.generalbytes.batm.server.extensions.Currencies;
import com.generalbytes.batm.server.extensions.IWallet;
import com.generalbytes.batm.server.extensions.extra.burstcoin.BurstAddressValidator;
import com.generalbytes.batm.server.extensions.extra.burstcoin.sources.crypto.client.BurstCryptoUtils;
import com.generalbytes.batm.server.extensions.extra.burstcoin.sources.crypto.client.Convert;
import com.generalbytes.batm.server.extensions.extra.burstcoin.sources.crypto.client.TransactionValidator;
import com.generalbytes.batm.server.extensions.extra.burstcoin.wallets.burstwallet.cgonline.AccountResponse;
import com.generalbytes.batm.server.extensions.extra.burstcoin.wallets.burstwallet.cgonline.BurstTransactionBroadcastResponse;
import com.generalbytes.batm.server.extensions.extra.burstcoin.wallets.burstwallet.cgonline.BurstTransactionBytesResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import si.mazi.rescu.RestProxyFactory;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;

public class BurstWallet implements IWallet {
    private static final Logger log = LoggerFactory.getLogger("batm.master.BurstWallet");
    private static final BigDecimal NQT = new BigDecimal("100000000");
    private static final BigDecimal DEFAULT_FEE_IN_NXT = new BigDecimal(1);
    private static final String GET_ACCOUNT = "getAccount";

    private final String masterPassword;
    private final String publicKey;
    private final String accountId;

    private final BurstWalletAPI api;

    public BurstWallet(String masterPassword, String accountId, String nodeAddress) {
        this.masterPassword = masterPassword;
        this.publicKey = BurstCryptoUtils.getPublicKey(masterPassword);
        this.accountId = accountId;
        this.api = RestProxyFactory.createProxy(BurstWalletAPI.class, nodeAddress);
    }

    @Override
    public String getPreferredCryptoCurrency() {
        return Currencies.BURST;
    }

    @Override
    public Set<String> getCryptoCurrencies() {
        Set<String> result = new HashSet<>();
        result.add(Currencies.BURST);
        return result;
    }

    @Override
    public String getCryptoAddress(String cryptoCurrency) {
        if (!getCryptoCurrencies().contains(cryptoCurrency)) {
            log.error("Cryptocurrency " + cryptoCurrency + " not supported.");
            return null;
        }

        AccountResponse res = api.getAccount(accountId, GET_ACCOUNT);
        if (res == null) {
            log.debug("No response received.");
            return null;
        }

        return res.getAccountRS();
    }

    @Override
    public BigDecimal getCryptoBalance(String cryptoCurrency) {
        if (!getCryptoCurrencies().contains(cryptoCurrency)) {
            log.error("Cryptocurrency " + cryptoCurrency + " not supported.");
            return null;
        }

        AccountResponse res = api.getAccount(accountId, GET_ACCOUNT);
        if (res == null) {
            log.debug("No response received.");
            return null;
        }

        String balanceNQT = res.getBalanceNQT();
        if (balanceNQT == null) {
            return BigDecimal.ZERO;
        }

        return new BigDecimal(balanceNQT).divide(NQT);
    }

    @Override
    public String sendCoins(String destinationAddress, BigDecimal amount, String cryptoCurrency, String description) {
        if (!getCryptoCurrencies().contains(cryptoCurrency)) {
            log.error("Cryptocurrency " + cryptoCurrency + " not supported.");
            return null;
        }

        BigInteger recipientInt = BurstAddressValidator.getAccountIdFromRS(destinationAddress);
        if (recipientInt == null) {
            log.error("Invalid destination address");
            return null;
        }

        String recipient = recipientInt.toString();

        BurstTransactionBytesResponse transactionBytesResponse = api.sendMoney(recipient, publicKey, amount.multiply(NQT).stripTrailingZeros(), DEFAULT_FEE_IN_NXT.multiply(NQT).stripTrailingZeros(), 1440, "sendMoney", false);
        if (transactionBytesResponse == null || transactionBytesResponse.getUnsignedTransactionBytes() == null) {
            log.error("No response received for transaction bytes.");
            return null;
        }

        byte[] unsignedTransactionBytes = Convert.hexStringToByteArray(transactionBytesResponse.getUnsignedTransactionBytes());

        try {
            TransactionValidator.validateUnsignedTransaction(unsignedTransactionBytes, recipientInt.longValue(), amount.multiply(NQT).stripTrailingZeros().longValue(), DEFAULT_FEE_IN_NXT.multiply(NQT).stripTrailingZeros().longValue());
        } catch (IllegalArgumentException e) {
            log.error("Node response invalid.");
            return null;
        }

        String signedTransaction = Convert.toHexString(BurstCryptoUtils.signTransaction(unsignedTransactionBytes, masterPassword));

        BurstTransactionBroadcastResponse transactionBroadcastResponse = api.broadcastTransaction(signedTransaction, "broadcastTransaction");
        if (transactionBroadcastResponse == null || transactionBroadcastResponse.getTransaction() == null) {
            log.error("No response received for transaction broadcast.");
            return null;
        }

        String transaction = transactionBroadcastResponse.getTransaction();
        log.debug(String.format("Transaction %s sent.", transaction));
        return transaction;
    }
}
