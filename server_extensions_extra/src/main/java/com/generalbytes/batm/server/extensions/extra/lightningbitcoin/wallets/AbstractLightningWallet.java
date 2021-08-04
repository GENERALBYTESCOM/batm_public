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
package com.generalbytes.batm.server.extensions.extra.lightningbitcoin.wallets;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.server.extensions.ILightningWallet;
import com.generalbytes.batm.server.extensions.IWalletInformation;
import com.generalbytes.batm.server.extensions.extra.lightningbitcoin.ILightningWalletInformation;
import com.generalbytes.batm.server.extensions.ThrowingSupplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Set;

public abstract class AbstractLightningWallet implements ILightningWallet, ILightningWalletInformation {
    private static final Logger log = LoggerFactory.getLogger(AbstractLightningWallet.class);

    @Override
    public String sendCoins(String destinationAddress, BigDecimal amount, BigDecimal fee, String cryptoCurrency, String description) {
        return sendCoins(destinationAddress, amount, cryptoCurrency, description);
    }

    @Override
    public String getCryptoAddress(String cryptoCurrency) {
        validateCryptoCurrency(cryptoCurrency);
        return getPubKey();
    }

    protected void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            log.trace("", e);
        }
    }

    @Override
    public IWalletInformation getWalletInformation() {
        return this;
    }

    @Override
    public Set<String> getCryptoCurrencies() {
        return null;
    }

    @Override
    public String getPreferredCryptoCurrency() {
        return null;
    }

    protected void validateCryptoCurrency(String cryptoCurrency) {
        if (!CryptoCurrency.LBTC.getCode().equals(cryptoCurrency)) {
            throw new IllegalArgumentException(cryptoCurrency + " not supported");
        }
    }

    /**
     * calls the supplier, logs the errors and returns null in case of exceptions
     *
     * @param supplier
     * @param <T>
     * @return null in case of exceptions
     */
    protected abstract <T> T callChecked(ThrowingSupplier<T> supplier);

    protected <T> T callChecked(String cryptoCurrency, ThrowingSupplier<T> supplier) {
        validateCryptoCurrency(cryptoCurrency);
        return callChecked(supplier);
    }

}
