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

package com.generalbytes.batm.server.extensions.extra.nxt.wallets.mynxt;

import com.generalbytes.batm.server.extensions.ICurrencies;
import com.generalbytes.batm.server.extensions.IWallet;
import com.generalbytes.batm.server.extensions.extra.nxt.NXTAddressValidator;
import com.generalbytes.batm.server.extensions.extra.nxt.wallets.mynxt.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import si.mazi.rescu.RestProxyFactory;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;

public class MynxtWallet implements IWallet{
    private static final Logger log = LoggerFactory.getLogger("batm.master.MynxtWallet");
    private static final BigDecimal NQT = new BigDecimal("100000000");
    private static final BigDecimal DEFAULT_FEE_IN_NXT = new BigDecimal(1);

    private String email;
    private String password;
    private String masterPassword;
    private String accountId;

    private IMynxtWalletAPI api;

    public MynxtWallet(String email, String password, String masterPassword, String accountId) {
        this.email = email;
        this.password = password;
        this.masterPassword = masterPassword;
        this.accountId = accountId;
        api = RestProxyFactory.createProxy(IMynxtWalletAPI.class, "https://wallet.mynxt.info");
    }

    @Override
    public String getPreferredCryptoCurrency() {
        return ICurrencies.NXT;
    }

    @Override
    public Set<String> getCryptoCurrencies() {
        Set<String> result = new HashSet<String>();
        result.add(ICurrencies.NXT);
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
        MynxtAccountsResponse res = api.getAllAccounts(email, password);
        if (res != null) {
            if (res.getStatus().equalsIgnoreCase("success")) {
                Account[] accounts = res.getData().getAccounts();
                Account selectedAccount = accounts[0];
                accountId = selectedAccount.getTx_account_rs();
                return accountId;
            }else{
                log.debug("No success received: " + res.getStatus() + " " + res.getMessage());
            }
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
            AccountResponse account = api.getAccount(email, password, accountId,"getAccount");
            if (account != null) {
                String balanceNQT = account.getBalanceNQT();
                if (balanceNQT == null) {
                    return BigDecimal.ZERO;
                }else{
                    return new BigDecimal(balanceNQT).divide(NQT);
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
        String accId = NXTAddressValidator.getAccountIdFromRS(accountId) +"";
        String recipient = destinationAddress;

        BigInteger recipientInt = NXTAddressValidator.getAccountIdFromRS(destinationAddress);
        if (recipientInt != null) {
            recipient = recipientInt.toString();
        }

        SendResponse res = api.send2(email, password, masterPassword, accId, recipient, amount.multiply(NQT).stripTrailingZeros(), DEFAULT_FEE_IN_NXT.multiply(NQT).stripTrailingZeros(), 1440, "sendMoney");
        if (res != null) {
            log.debug("Transaction " + res.getTransaction() + " sent.");
            return res.getTransaction();
        }else{
            log.debug("No response received.");
        }
        return null;
    }
}
