/*************************************************************************************
 * Copyright (C) 2014-2020 GENERAL BYTES s.r.o. All rights reserved.
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
package com.generalbytes.batm.server.extensions.extra.bitcoincash.wallets.telr;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
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

public class TelrCashWallet implements IWallet {
    private static final Logger log = LoggerFactory.getLogger(TelrCashWallet.class);
    private static final BigDecimal coin = BigDecimal.valueOf(Math.pow(10, 8));
    private static final Integer readTimeout = 90 * 1000; // 90 seconds

    private final ITelrCashProxyAPI api;

    /* Variables. */
    private String address;
    private String secret;
    private String signature;

    private BigDecimal fromSatoshis(long amount) {
        return BigDecimal.valueOf(amount).divide(coin);
    }

    private long toSatoshis(BigDecimal amount) {
        return amount.multiply(coin).longValue();
    }

    public TelrCashWallet(String address, String secret, String signature) {
        String proxyUrl = "https://api.telr.io/";

        this.address = address;
        this.secret = secret;
        this.signature = signature;

        ClientConfig config = new ClientConfig();
        config.setHttpReadTimeout(readTimeout);
        api = RestProxyFactory.createProxy(ITelrCashProxyAPI.class, proxyUrl, config);
    }

    @Override
    public String getCryptoAddress(String cryptoCurrency) {
        try {
            return api.getCryptoAddress(
                this.address,
                this.secret,
                this.signature,
                cryptoCurrency
            );
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
            return new BigDecimal(api.getCryptoBalance(
                this.address,
                this.secret,
                this.signature,
                cryptoCurrency
            ));
        } catch (HttpStatusIOException e) {
            log.error(e.getHttpBody());
        } catch (IOException e) {
            log.error("", e);
        }
        return null;
    }

    @Override
    public String sendCoins(
        String destinationAddress,
        BigDecimal amount,
        String cryptoCurrency,
        String description
    ) {
        try {
            // NOTE a successful tx will return an empty string ("" or NULL)
            return api.sendCoins(
                this.address,
                this.secret,
                this.signature,
                cryptoCurrency,
                destinationAddress,
                toSatoshis(amount),
                description
            );
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
        s.add(CryptoCurrency.BCH.getCode());
        s.add(CryptoCurrency.BTC.getCode());
        s.add(CryptoCurrency.DAI.getCode());
        s.add(CryptoCurrency.ETH.getCode());
        s.add(CryptoCurrency.LTC.getCode());
        return s;
    }

    @Override
    public String getPreferredCryptoCurrency() {
        return CryptoCurrency.BTC.getCode();
    }

}
