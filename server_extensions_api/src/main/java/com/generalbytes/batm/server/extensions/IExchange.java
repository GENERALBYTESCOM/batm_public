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

package com.generalbytes.batm.server.extensions;

import java.math.BigDecimal;
import java.util.Set;

/**
 * This interface is used by master to purchase coins on crypto exchange like Bitstamp.net
 */
public interface IExchange {
    /**
     * Returns set of cryptocurrencies that crypto exchange supports
     * @return
     */
    public Set<String> getCryptoCurrencies();

    /**
     * Returns list of fiat currencies that crypto exchange supports (USD,EUR etc)
     * @return
     */
    public Set<String> getFiatCurrencies();

    /**
     * Returns fiat currency that is used for actual purchases of cryptocurrency by server
     * @return
     */
    public String getPreferredFiatCurrency();

    /**
     * Returns current balance of cryptocurrency on the exchange
     * @param cryptoCurrency
     * @return
     */
    public BigDecimal getCryptoBalance(String cryptoCurrency);

    /**
     * Returns current balance of fiat money on the exchange
     * @param fiatCurrency
     * @return
     */
    public BigDecimal getFiatBalance(String fiatCurrency);

    /**
     * This method is used for purchasing coins on the exchange using specified fiat currency to use.
     * If something fails this method should return NULL otherwise it should return orderId or any other identifier that exchange provides for later tracking
     *
     * @param amount
     * @param cryptoCurrency
     * @param fiatCurrencyToUse
     * @param description
     * @return
     */
    public String purchaseCoins(BigDecimal amount, String cryptoCurrency, String fiatCurrencyToUse, String description);

    /**
     * This method is used for selling coins on the exchange for specified fiat currency.
     * If something fails this method should return NULL otherwise it should return orderId or any other identifier that exchange provides for later tracking
     *
     * @param cryptoAmount
     * @param cryptoCurrency
     * @param fiatCurrencyToUse
     * @param description
     * @return
     */
    public String sellCoins(BigDecimal cryptoAmount, String cryptoCurrency, String fiatCurrencyToUse, String description);

    /**
     * This method is used for withdrawing coins from the exchange by sending them to specified address
     *
     * @param destinationAddress
     * @param amount
     * @param cryptoCurrency
     * @param description
     * @return
     */
    public String sendCoins(String destinationAddress, BigDecimal amount, String cryptoCurrency, String description);

    /**
     * This method is used when depositing coins on exchange (address can be new with every call)
     * @param cryptoCurrency
     * @return
     */
    public String getDepositAddress(String cryptoCurrency);

}
