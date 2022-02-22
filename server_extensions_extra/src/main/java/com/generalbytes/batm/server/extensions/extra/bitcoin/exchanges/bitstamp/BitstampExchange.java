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
package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.bitstamp;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.common.currencies.FiatCurrency;
import com.generalbytes.batm.server.extensions.coinutil.BCHUtil;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.XChangeExchange;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.dto.account.AccountInfo;
import org.knowm.xchange.dto.account.Wallet;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class BitstampExchange extends XChangeExchange {

    private final Set<String> cryptoCurrencies = Collections.unmodifiableSet(new HashSet<String>() {
        {
            add(CryptoCurrency.BCH.getCode());
            add(CryptoCurrency.BTC.getCode());
            add(CryptoCurrency.ETH.getCode());
            add(CryptoCurrency.LTC.getCode());
            add(CryptoCurrency.XRP.getCode());
        }
    });
    private final Set<String> fiatCurrencies = Collections.unmodifiableSet(new HashSet<String>() {
        {
            add(FiatCurrency.EUR.getCode());
            add(FiatCurrency.GBP.getCode());
            add(FiatCurrency.USD.getCode());
        }
    });

    public BitstampExchange(String preferredFiatCurrency) {
        super(getDefaultSpecification(), preferredFiatCurrency);
    }

    public BitstampExchange(String preferredFiatCurrency, String userId, String key, String secret) {
        super(getSpecification(userId, key, secret), preferredFiatCurrency);
    }

    private static ExchangeSpecification getDefaultSpecification() {
        return new org.knowm.xchange.bitstamp.BitstampExchange().getDefaultExchangeSpecification();
    }

    private static ExchangeSpecification getSpecification(String userId, String key, String secret) {
        ExchangeSpecification spec = getDefaultSpecification();
        spec.setUserName(userId);
        spec.setApiKey(key);
        spec.setSecretKey(secret);
        return spec;
    }

    @Override
    public Set<String> getCryptoCurrencies() {
        return cryptoCurrencies;
    }

    @Override
    public Set<String> getFiatCurrencies() {
        return fiatCurrencies;
    }

    @Override
    protected boolean isWithdrawSuccessful(String result) {
        return result != null;
    }

    @Override
    protected double getAllowedCallsPerSecond() {
        return 10;
    }

    @Override
    public Wallet getWallet(AccountInfo accountInfo, String currency) {
        return accountInfo.getWallet();
    }

    @Override
    public String sendCoins(String destinationAddress, BigDecimal amount, String cryptoCurrency, String description) {
        if (CryptoCurrency.BCH.getCode().equalsIgnoreCase(cryptoCurrency)) {
            destinationAddress = BCHUtil.convertBech32To3(destinationAddress);
        }
        return super.sendCoins(destinationAddress, amount, cryptoCurrency, description);
    }

    @Override
    public String getDepositAddress(String cryptoCurrency) {
        try {
            if (CryptoCurrency.XRP.getCode().equals(cryptoCurrency)) {
                // requestDepositAddressData not implemented in xchange-bitstamp,
                // requestDepositAddress returns for example: rDsbeomae4FXwgQTJp9Rs64Qg9vDiTCdBv?dt=47976641
                return getExchange().getAccountService()
                    .requestDepositAddress(Currency.getInstance(cryptoCurrency))
                    .replace("?dt=", ":");
            }
            return super.getDepositAddress(cryptoCurrency);
        } catch (IOException e) {
            log.error("Error", e);
            return null;
        }
    }
}