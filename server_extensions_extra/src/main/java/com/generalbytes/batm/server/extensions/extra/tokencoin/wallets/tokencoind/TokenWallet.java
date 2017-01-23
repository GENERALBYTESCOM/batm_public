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

package com.generalbytes.batm.server.extensions.extra.tokencoin.wallets.tokencoind;

import com.generalbytes.batm.server.extensions.ICurrencies;
import com.generalbytes.batm.server.extensions.IWallet;
import com.generalbytes.batm.server.extensions.extra.tokencoin.wallets.tokencoind.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import si.mazi.rescu.RestProxyFactory;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

public class TokenWallet implements IWallet{
    private static final Logger log = LoggerFactory.getLogger("batm.master.Tokenwallet");
    private static final BigDecimal NQT = new BigDecimal("100000000");
    private static final BigDecimal DEFAULT_FEE_IN_TKN = new BigDecimal(1);

    private String host;
    private int port;
    private String accountId;

    private TokenWalletAPI api;

    public TokenWallet(String host, int port, String accountId) {
        this.host = host;
        this.port = port;
        this.accountId = accountId;
        api = RestProxyFactory.createProxy(TokenWalletAPI.class, "http://" +host+":"+port);
    }

    @Override
    public String getPreferredCryptoCurrency() {
        return ICurrencies.TKN;
    }

    @Override
    public Set<String> getCryptoCurrencies() {
        Set<String> result = new HashSet<String>();
        result.add(ICurrencies.TKN);
        return result;
    }

    @Override
    public String getCryptoAddress(String cryptoCurrency) {
        if (!getCryptoCurrencies().contains(cryptoCurrency)) {
            log.error("Cryptocurrency " + cryptoCurrency + " not supported.");
            return null;
        }
        if (accountId != null) {
            return accountId;
        }
        TokenAccountsResponse res = api.getAllAccounts();
        if (res != null) {
                Account[] accounts = res.getData().getAccounts();
                Account selectedAccount = accounts[0];
                accountId = selectedAccount.getId();
                return accountId;
        }else{
            log.debug("No response received.");
        }
        return null;
    }

    @Override
    public BigDecimal getCryptoBalance(String cryptoCurrency) {
        if (!getCryptoCurrencies().contains(cryptoCurrency)) {
            log.error("Cryptocurrency " + cryptoCurrency + " not supported.");
            return null;
        }
        if (accountId == null) {
            getCryptoAddress(cryptoCurrency); //to load account_id
        }
        if (accountId != null) {
            AccountResponse account = api.getBalance(accountId);
            if (account != null) {
                double balanceNQT = account.getFunds();
                System.out.println("THIS IS THE WALLET " +account.toString() + " " + account.getFunds());
                if (balanceNQT <= 0) {
                    return BigDecimal.ZERO;
                }else{
                    return new BigDecimal(balanceNQT);
                }
            }else{
                log.debug("No response received.");
            }
        }
        return null;
    }

    @Override
    public String sendCoins(String destinationAddress, BigDecimal amount, String cryptoCurrency, String description) {
        if (!getCryptoCurrencies().contains(cryptoCurrency)) {
            log.error("Cryptocurrency " + cryptoCurrency + " not supported.");
            return null;
        }
        if (accountId == null) {
            getCryptoAddress(cryptoCurrency); //to load account_id
        }
        //String accId = TKNAddressValidator.getAccountIdFromRS(accountId) +"";
        String accId = accountId;
        String recipient = destinationAddress;

        /* BigInteger recipientInt = TKNAddressValidator.getAccountIdFromRS(destinationAddress);
        if (recipientInt != null) {
            recipient = recipientInt.toString();
        }*/

        SendResponse res = api.send2( accId, recipient, amount.stripTrailingZeros(), "sendMoney");
        if (res != null) {
            log.debug("Transaction " + res.getTransaction() + " sent.");
            return res.getTransaction();
        }else{
            log.debug("No response received.");
        }
        return null;
    }
}
