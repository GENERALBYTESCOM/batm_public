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
import com.generalbytes.batm.server.extensions.extra.burstcoin.wallets.burstwallet.cgonline.AccountResponse;
import com.generalbytes.batm.server.extensions.extra.burstcoin.wallets.burstwallet.cgonline.SendResponse;
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
    private final String accountId;

    private final BurstWalletAPI api;

    public BurstWallet(String masterPassword, String accountId) {
        this.masterPassword = masterPassword;
        this.accountId = accountId;
        this.api = RestProxyFactory.createProxy(BurstWalletAPI.class, "https://wallet.burst.cryptoguru.org:8125");
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
            log.debug("Invalid destination address"); // Error?
            return null;
        }

        String recipient = recipientInt.toString();

        SendResponse res = api.send(masterPassword, accountId, recipient, amount.multiply(NQT).stripTrailingZeros(), DEFAULT_FEE_IN_NXT.multiply(NQT).stripTrailingZeros(), 1440, "sendMoney");
        if (res == null) {
            log.debug("No response received."); // Error?
            return null;
        }

        String transaction = res.getTransaction();
        log.debug(String.format("Transaction %s sent.", transaction));
        return transaction;
    }
}
