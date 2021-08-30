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
import java.util.Set;

/**
 * IWallet is accessed by server to manipulate with funds (typically cryptocurrency)
 */
public interface IWallet {
    /**
     * Returns crypto address of a wallet
     * @param cryptoCurrency
     * @return
     */
    String getCryptoAddress(String cryptoCurrency);

    /**
     * Returns list of crypto-currency symbols that wallet supports
     * @return
     */
    Set<String> getCryptoCurrencies();

    /**
     * Returns main/preferred crypto-currency of a wallet
     * @return
     */
    String getPreferredCryptoCurrency();

    /**
     * Returns current balance of given crypto-currency (usually confirmed+unconfirmed)
     * @param cryptoCurrency
     * @return
     */
    BigDecimal getCryptoBalance(String cryptoCurrency);

    /**
     * Commands wallet to send coins from wallet to a different address. Description contains remote transaction id
     * @param destinationAddress
     * @param amount
     * @param cryptoCurrency
     * @param description
     * @return - method must return non-null string. If it returns null it will be considered as error. Usually txid is returned and stored in server's database.
     */
    String sendCoins(String destinationAddress, BigDecimal amount, String cryptoCurrency, String description); //returns txid


}
