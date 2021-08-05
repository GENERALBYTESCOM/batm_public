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

public interface IWalletAdvanced extends IWallet {
    /**
     * Commands wallet to send coins from wallet to a different address. Description contains remote transaction id
     * @param destinationAddress
     * @param amount
     * @param fee
     * @param cryptoCurrency
     * @param description
     * @return - method must return non-null string. If it returns null it will be considered as error. Usually txid is returned and stored in server's database.
     */
    String sendCoins(String destinationAddress, BigDecimal amount, BigDecimal fee, String cryptoCurrency, String description); //returns txid

    /**
     * Returns optional additional information about wallet.
     * @return - may return null
     */
    IWalletInformation getWalletInformation();
}
