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
package com.generalbytes.batm.server.extensions;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

public interface ITransactionSellInfo {
    /**
     * Returns unique (in server scope) transaction id. It is generated by server.
     * @return
     */
    String getRemoteTransactionId();
    /**
     * Returns transaction id generated locally by terminal to perform request on server.
     * Don't use it if you don't have to. It is used only for time before server assignes remote transaction id to a transaction.
     * @return
     */
    String getLocalTransactionId();

    /**
     * Returns status of the transaction
     * @return
     */
    int getStatus();

    /**
     * Fiat amount
     * @return
     */
    BigDecimal getCashAmount();

    /**
     * Fiat currency code (USD, EUR etc)
     * @return
     */
    String getCashCurrency() ;

    /**
     * Amount of coins - crypto-currency
     * @return
     */
    BigDecimal getCryptoAmount();

    /**
     * Cryptocurrency code (BTC, ETH etc)
     * @return
     */
    String getCryptoCurrency() ;

    /**
     * Secret code that must be encoded as URL parameter uuid embeded in QR code of redeem ticket
     * Example: bitcoin:1BvBMSEYstWetqTFn5Au4m4GFg7xJaNVN2?amount=0.1&label=RTPSM&uuid=XISISIISKSSJK
     * @return
     */
    String getTransactionUUID();

    /**
     * Destination address where the coins are supposed to be sent to by customer
     * @return
     */
    String getCryptoAddress();

    /**
     * Returns maximum time in which coins should be sent by customer to Crypto Address after that the sell will timeout
     * @return
     */
    long getValidityInMinutes();

    /**
     * Returns what was the fixed transaction fee in fiat currency used for the transaction
     * @return
     */
    BigDecimal getFixedTransactionFee();

    /**
     * Custom texts that should be written on the ticket. Custom data are usually generated by ITransactionListener
     * @return
     */
    Map<String, String> getCustomData();

    /**
     * Server time of the transaction
     *
     * @return {@link Date}
     */
    Date getServerTime();

    /**
     * Terminal time of the transaction - calculated from the terminal location timezone (you may have terminals across multiple time zones)
     *
     * @return {@link Date}
     */
    Date getTerminalTime();
}
