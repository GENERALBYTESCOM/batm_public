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
package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.ftx;

import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.XChangeExchange;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.dto.account.AccountInfo;
import org.knowm.xchange.dto.account.Wallet;

import java.math.BigDecimal;

public abstract class FtxExchange extends XChangeExchange {
    /*
     * https://help.ftx.com/hc/en-us/articles/360027946651-Order-Limits-and-Price-Bands
     * If an input size or price is not divisible by the market's size step or tick size,
     * respectively, then we do not reject the order: we round the input, conservatively:
     * - Size gets rounded down to the nearest size step increment
     * - Price gets rounded up (if selling) or down (of buying) to the nearest price increment
     */

    public FtxExchange(String preferredFiatCurrency, String sslUri) {
        super(getDefaultSpecification(sslUri), preferredFiatCurrency);
    }

    public FtxExchange(String key, String secret, String preferredFiatCurrency, String sslUri) {
        super(getSpecification(key, secret, sslUri), preferredFiatCurrency);
    }

    private static ExchangeSpecification getDefaultSpecification(String sslUri) {
        ExchangeSpecification spec = new org.knowm.xchange.ftx.FtxExchange().getDefaultExchangeSpecification();
        spec.setSslUri(sslUri);
        return spec;
    }

    private static ExchangeSpecification getSpecification(String key, String secret, String sslUri) {
        ExchangeSpecification spec = getDefaultSpecification(sslUri);
        spec.setApiKey(key);
        spec.setSecretKey(secret);
        return spec;
    }

    @Override
    protected boolean isWithdrawSuccessful(String result) {
        return true;
    }

    @Override
    protected double getAllowedCallsPerSecond() {
        return 10;
    }

    @Override
    public Wallet getWallet(AccountInfo accountInfo, String currency) {
        return accountInfo.getWallet(Wallet.WalletFeature.TRADING);
    }

    @Override
    public String sendCoins(String destinationAddress, BigDecimal amount, String cryptoCurrency, String description) {
        log.warn("sendCoins not yet supported, this exchange cannot be used with the current exchange strategy");
        return null;
    }

    @Override
    public String getDepositAddress(String cryptoCurrency) {
        log.warn("getDepositAddress not yet supported, this exchange cannot be used with the current exchange strategy");
        return null;
    }
}