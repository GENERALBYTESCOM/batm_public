/*************************************************************************************
 * Copyright (C) 2014-2016 GENERAL BYTES s.r.o. All rights reserved.
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
package com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.bitcore;

import com.generalbytes.batm.server.extensions.ICurrencies;
import com.generalbytes.batm.server.extensions.IWallet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import si.mazi.rescu.ClientConfig;
import si.mazi.rescu.HttpStatusIOException;
import si.mazi.rescu.RestProxyFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

public class BitcoreWallet implements IWallet {
    private static final Logger log = LoggerFactory.getLogger(BitcoreWallet.class);
    private static final BigDecimal coin = BigDecimal.valueOf(Math.pow(10, 8));
    private static final Integer readTimeout = 90 * 1000; //90 seconds

    private final String apiKey;
    private final IBitcoreProxyAPI api;

    private BigDecimal fromSatoshis(long amount) {
        return BigDecimal.valueOf(amount).divide(coin);
    }

    private long toSatoshis(BigDecimal amount) {
        return amount.multiply(coin).longValue();
    }

    public BitcoreWallet(String apiKey, String proxyUrl) {
        this.apiKey = apiKey;
        ClientConfig config = new ClientConfig();
        config.setHttpReadTimeout(readTimeout);
        api = RestProxyFactory.createProxy(IBitcoreProxyAPI.class, proxyUrl, config);
    }

    @Override
    public String getCryptoAddress(String cryptoCurrency) {
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
        try {
            return fromSatoshis(api.getCryptoBalance(cryptoCurrency));
        } catch (HttpStatusIOException e) {
            log.error(e.getHttpBody());
        } catch (IOException e) {
            log.error("", e);
        }
        return null;
    }

    @Override
    public String sendCoins(String destinationAddress, BigDecimal amount, String cryptoCurrency, String description) {
        try {
            return api.sendCoins(this.apiKey, destinationAddress, toSatoshis(amount), cryptoCurrency, description);
        } catch (HttpStatusIOException e) {
            log.error(e.getHttpBody());
        } catch (IOException e) {
            log.error("", e);
        }
        return null;
    }

    @Override
    public Set<String> getCryptoCurrencies() {
        HashSet<String> s = new HashSet<String>();
        s.add(ICurrencies.BTC);
        return s;
    }

    @Override
    public String getPreferredCryptoCurrency() {
        return ICurrencies.BTC;
    }
}
