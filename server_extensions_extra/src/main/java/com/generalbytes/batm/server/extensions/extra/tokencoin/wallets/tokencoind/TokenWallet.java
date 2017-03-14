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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import si.mazi.rescu.HttpStatusIOException;
import si.mazi.rescu.RestProxyFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

public class TokenWallet implements IWallet{
    private static final Logger log = LoggerFactory.getLogger("batm.master.Tokenwallet");

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
        if (accountId != null) {
            return accountId;
        }
        try {
            return api.getAddress(cryptoCurrency);
        } catch (HttpStatusIOException e) {
            log.error(e.getHttpBody());
        } catch (IOException e) {
            log.error("", e);
        }
        return null;
    }

    @Override
    public BigDecimal getCryptoBalance(String cryptoCurrency) {
        if (accountId == null) {
            getCryptoAddress(cryptoCurrency); //to load account_id
        }
        try {
            Double amount = api.getCryptoBalance(accountId);
            if (amount == null) {
                return BigDecimal.ZERO;
            }else{
                return new BigDecimal(amount);
            }
        } catch (HttpStatusIOException e) {
            log.error(e.getHttpBody());
        } catch (IOException e) {
            log.error("", e);
        }
        return null;
    }

    @Override
    public String sendCoins(String destinationAddress, BigDecimal amount, String cryptoCurrency, String description) {
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
        try{
            return api.send2( accountId, destinationAddress, amount.stripTrailingZeros(), "sendMoney");
        } catch (HttpStatusIOException e) {
            log.error(e.getHttpBody());
        } catch (IOException e) {
            log.error("", e);
        }
        return null;
    }
}
