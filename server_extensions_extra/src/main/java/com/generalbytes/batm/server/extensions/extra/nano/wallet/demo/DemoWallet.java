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
package com.generalbytes.batm.server.extensions.extra.nano.wallet.demo;

import com.generalbytes.batm.server.extensions.ICanSendMany;
import com.generalbytes.batm.server.extensions.IExchange;
import com.generalbytes.batm.server.extensions.IRateSource;
import com.generalbytes.batm.server.extensions.IWallet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.*;

public class DemoWallet implements IExchange, IWallet, IRateSource, ICanSendMany {

    private static final Logger log = LoggerFactory.getLogger(DemoWallet.class);

    private static final BigDecimal EXCHANGE_RATE = new BigDecimal(2000);
    private static final BigDecimal WALLET_BALANCE = new BigDecimal(10);
    private static final BigDecimal EXCHANGE_BALANCE = new BigDecimal(1000);
    private final String fiatCurrency, cryptoCurrency, walletAddress;


    public DemoWallet(String fiatCurrency, String cryptoCurrency, String walletAddress) throws IllegalArgumentException {
        this.fiatCurrency = fiatCurrency;
        this.cryptoCurrency = cryptoCurrency;
        this.walletAddress = walletAddress;
    }

    @Override
    public Set<String> getCryptoCurrencies() {
        return Collections.singleton(cryptoCurrency);
    }

    @Override
    public Set<String> getFiatCurrencies() {
        return Collections.singleton(fiatCurrency);
    }

    @Override
    public String getPreferredFiatCurrency() {
        return fiatCurrency;
    }

    @Override
    public BigDecimal getCryptoBalance(String cryptoCurrency) {
        return WALLET_BALANCE;
    }

    @Override
    public BigDecimal getFiatBalance(String fiatCurrency) {
        if (this.fiatCurrency.equalsIgnoreCase(fiatCurrency)) {
            return EXCHANGE_BALANCE;
        }else{
            return BigDecimal.ZERO;
        }
    }

    @Override
    public String purchaseCoins(BigDecimal amount, String cryptoCurrency, String fiatCurrencyToUse, String description) {
        if (cryptoCurrency.equalsIgnoreCase(this.cryptoCurrency) && fiatCurrencyToUse.equalsIgnoreCase(this.fiatCurrency)) {
            String id = UUID.randomUUID().toString();
            log.info("{}-DummyExchangeWallet: purchasing coins {}, id: {}", this.cryptoCurrency, amount, id);
            return id;
        } else {
            log.info("{}-DummyExchangeWallet: unsupported currency {}", this.cryptoCurrency, cryptoCurrency);
            return null;
        }
    }

    @Override
    public String sellCoins(BigDecimal cryptoAmount, String cryptoCurrency, String fiatCurrencyToUse, String description) {
        if (cryptoCurrency.equalsIgnoreCase(this.cryptoCurrency) && fiatCurrencyToUse.equalsIgnoreCase(this.fiatCurrency)) {
            String id = UUID.randomUUID().toString();
            log.info("{}-DummyExchangeWallet: selling coins {}, id: {}", this.cryptoCurrency, cryptoAmount, id);
            return id;
        } else {
            log.info("{}-DummyExchangeWallet: unsupported currency {}", this.cryptoCurrency, cryptoCurrency);
            return null;
        }
    }

    @Override
    public String sendCoins(String destinationAddress, BigDecimal amount, String cryptoCurrency, String description) {
        if (cryptoCurrency.equalsIgnoreCase(this.cryptoCurrency)) {
            String id = UUID.randomUUID().toString();
            log.info("{}-DummyExchangeWallet: sending coins to {} {}, id: {}",
                    this.cryptoCurrency, destinationAddress, amount, id);
            return id;
        } else {
            log.info("{}-DummyExchangeWallet: unsupported currency {}", this.cryptoCurrency, cryptoCurrency);
            return null;
        }
    }

    @Override
    public String sendMany(Collection<Transfer> transfers, String cryptoCurrency, String description) {
        if (cryptoCurrency.equalsIgnoreCase(this.cryptoCurrency)) {
            String id = UUID.randomUUID().toString();
            log.info("{}-DummyExchangeWallet: sendMany: {} {}, id: {}",
                    this.cryptoCurrency, transfers, description, id);
            return id;
        } else {
            log.info("{}-DummyExchangeWallet: unsupported currency {}", this.cryptoCurrency, cryptoCurrency);
            return null;
        }
    }

    @Override
    public String getDepositAddress(String cryptoCurrency) {
        return getAddress(cryptoCurrency);
    }

    private String getAddress(String cryptoCurrency) {
        return this.cryptoCurrency.equals(cryptoCurrency) ? walletAddress : null;
    }

    @Override
    public BigDecimal getExchangeRateLast(String cryptoCurrency, String fiatCurrency) {
        log.info("{}-DummyExchangeWallet: exchange rate is {}", this.cryptoCurrency, EXCHANGE_RATE);
        return EXCHANGE_RATE;
    }

    @Override
    public String getCryptoAddress(String cryptoCurrency) {
        return getAddress(cryptoCurrency);

    }

    @Override
    public String getPreferredCryptoCurrency() {
        return cryptoCurrency;
    }

}
