/*************************************************************************************
 * Copyright (C) 2014-2016 GENERAL BYTES s.r.o. All rights reserved.
 * <p/>
 * This software may be distributed and modified under the terms of the GNU
 * General Public License version 2 (GPL2) as published by the Free Software
 * Foundation and appearing in the file GPL2.TXT included in the packaging of
 * this file. Please note that GPL2 Section 2[b] requires that all works based
 * on this software must also be made publicly available under the terms of
 * the GPL2 ("Copyleft").
 * <p/>
 * Contact information
 * -------------------
 * <p/>
 * GENERAL BYTES s.r.o.
 * Web      :  http://www.generalbytes.com
 ************************************************************************************/
package com.generalbytes.batm.server.extensions.extra.shadowcash.wallets.shadowcashd;

import com.generalbytes.batm.server.extensions.ICurrencies;
import com.generalbytes.batm.server.extensions.IWallet;
import com.rutherford.jsonrpc.CryptocoinClientFactory;
import com.rutherford.jsonrpc.ShadowcashdInterface;
import com.rutherford.jsonrpc.value.shadowcash.ShadowInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

public class ShadowcashdRPCWallet implements IWallet {

    private static final Logger log = LoggerFactory.getLogger(ShadowcashdRPCWallet.class);
    private static final String CRYPTO_CURRENCY = ICurrencies.SDC;

    private static ShadowcashdInterface client;

    public ShadowcashdRPCWallet(final String rpcURL, final String username, final String password) {
        client = createClient(rpcURL, username, password);
    }

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
            log.error("Shadowcashd wallet error: unknown cryptocurrency.");
            return null;
        }

        log.info("Shadowcashd sending {} coins to: {} ", amount.doubleValue(), destinationAddress);
        String result = client.sendtoaddress(destinationAddress, amount);
        log.debug("result: {} ", result);
        return result;
    }

    @Override
    public String getCryptoAddress(String cryptoCurrency) {
        if (!CRYPTO_CURRENCY.equalsIgnoreCase(cryptoCurrency)) {
            log.error("Shadowcashd wallet error: unknown cryptocurrency: {}", cryptoCurrency);
            return null;
        }

        String newAddress = client.getnewaddress();
        return newAddress;
    }

    @Override
    public BigDecimal getCryptoBalance(String cryptoCurrency) {
        if (!CRYPTO_CURRENCY.equalsIgnoreCase(cryptoCurrency)) {
            log.error("Shadowcashd wallet error: unknown cryptocurrency: {}", cryptoCurrency);
            return null;
        }
        return client.getbalance();
    }

    public ShadowInfo getInfo() {
        return client.getinfo();
    }

    private static ShadowcashdInterface createClient(final String rpcURL, final String username, final String password) {
        try {
            final CryptocoinClientFactory clientFactory = new CryptocoinClientFactory(new URL(rpcURL), username, password);
            return clientFactory.getClient(ShadowcashdInterface.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
