/*************************************************************************************
 * Copyright (C) 2014-2025 GENERAL BYTES s.r.o. All rights reserved.
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
package com.generalbytes.batm.server.extensions.util;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.server.extensions.DummyExchangeAndWalletAndSource;

import java.util.NoSuchElementException;
import java.util.StringTokenizer;

/**
 * Factory for creating {@link DummyExchangeAndWalletAndSource}.
 */
public class DummyWalletAndExchangeAndSourceFactory {

    /**
     * Create a {@link DummyExchangeAndWalletAndSource} for the given cryptocurrency with the given parameters.
     *
     * <p><b>Required Parameters:</b></p>
     * <ol>
     *     <li>fiat_currency</li>
     *     <li>address</li>
     * </ol>
     *
     * <p><b>Example Parameters:</b> {@code USD:18nB5x3zxF26MuA89yNcnkS9qs33KNwLFu}</p>
     *
     * @param parameters     The parameters as a StringTokenizer.
     * @param cryptocurrency The cryptocurrency.
     * @return The created {@link DummyExchangeAndWalletAndSource}.
     * @throws NoSuchElementException   If there are not enough parameters in the StringTokenizer.
     * @throws IllegalArgumentException If the parameters are invalid.
     */
    public DummyExchangeAndWalletAndSource createDummyWithFiatCurrencyAndAddress(StringTokenizer parameters,
                                                                                 CryptoCurrency cryptocurrency) {
        String fiatCurrency = parameters.nextToken();
        String walletAddress = parameters.nextToken();

        return new DummyExchangeAndWalletAndSource(fiatCurrency, cryptocurrency.getCode(), walletAddress);
    }
}
