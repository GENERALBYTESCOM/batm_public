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
package com.generalbytes.batm.server.extensions.extra.slp.wallets;

import com.generalbytes.batm.common.currencies.SlpToken;
import com.generalbytes.batm.server.coinutil.AddressFormatException;
import com.generalbytes.batm.server.extensions.IQueryableWallet;
import com.generalbytes.batm.server.extensions.IWallet;
import com.generalbytes.batm.server.extensions.ThrowingSupplier;
import com.generalbytes.batm.server.extensions.payment.ReceivedAmount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import si.mazi.rescu.HttpStatusIOException;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.ConnectException;
import java.util.Set;

public abstract class SlpWallet implements IWallet, IQueryableWallet {
    private static final Logger log = LoggerFactory.getLogger(SlpWallet.class);

    @Override
    public Set<String> getCryptoCurrencies() {
        return SlpToken.SLP_TOKENS.keySet();
    }

    @Override
    public String getPreferredCryptoCurrency() {
        return null; // not used
    }

    @Override
    public String sendCoins(String destinationAddress, BigDecimal amount, String cryptoCurrency, String description) {
        return callChecked(cryptoCurrency, () -> {
            BigDecimal amountRounded = amount.setScale(SlpToken.valueOf(cryptoCurrency).getDecimals(), RoundingMode.HALF_UP);
            return sendCoinsInternal(destinationAddress, amountRounded, cryptoCurrency, description);
        });
    }


    public String getCryptoAddress(String cryptoCurrency) {
        return callChecked(cryptoCurrency, () -> getCryptoAddressInternal(cryptoCurrency));
    }

    public String generateNewDepositCryptoAddress(String cryptoCurrency, String label) {
        return callChecked(cryptoCurrency, () -> generateNewDepositCryptoAddressInternal(cryptoCurrency, label));
    }

    public BigDecimal getCryptoBalance(String cryptoCurrency) {
        return callChecked(cryptoCurrency, () -> getCryptoBalanceInternal(cryptoCurrency));
    }

    public ReceivedAmount getReceivedAmount(String address, String cryptoCurrency) {
        return callChecked(cryptoCurrency, () -> getReceivedAmountInternal(address, cryptoCurrency));
    }

    protected abstract BigDecimal getCryptoBalanceInternal(String cryptoCurrency) throws IOException;

    protected abstract String getCryptoAddressInternal(String cryptoCurrency) throws IOException, AddressFormatException;

    protected abstract String generateNewDepositCryptoAddressInternal(String cryptoCurrency, String label) throws IOException, AddressFormatException;

    protected abstract String sendCoinsInternal(String destinationAddress, BigDecimal amountRounded, String cryptoCurrency, String description) throws IOException;

    protected abstract ReceivedAmount getReceivedAmountInternal(String address, String cryptoCurrency) throws IOException, AddressFormatException;

    protected <T> T callChecked(String cryptoCurrency, ThrowingSupplier<T> supplier) {
        if (cryptoCurrency == null || !getCryptoCurrencies().contains(cryptoCurrency)) {
            log.error("cryptocurrency not supported: {}", cryptoCurrency);
            return null;
        }
        return callChecked(supplier);
    }

    protected <T> T callChecked(ThrowingSupplier<T> supplier) {
        try {
            return callCheckedCustom(supplier);
        } catch (HttpStatusIOException e) {
            log.error("REST call failed; HTTP status: {}, body: {}", e.getHttpStatusCode(), e.getHttpBody());
        } catch (ConnectException e) {
            log.error("Cannot connect", e);
        } catch (Exception e) {
            log.error("", e);
        }
        return null;
    }

    /**
     * can catch and handle custom exceptions, common exceptions are handled in {@link #callChecked(ThrowingSupplier)}
     *
     * @param supplier
     * @param <T>
     * @return
     * @throws Exception any exceptions not handled by the custom handler
     */
    protected <T> T callCheckedCustom(ThrowingSupplier<T> supplier) throws Exception {
        return supplier.get();
    }
}
