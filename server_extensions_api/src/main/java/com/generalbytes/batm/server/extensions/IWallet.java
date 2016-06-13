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

public interface IWallet {
    public String sendCoins(String destinationAddress, BigDecimal amount, String cryptoCurrency, String description); //returns txid
    public String getCryptoAddress(String cryptoCurrency);
    public Set<String> getCryptoCurrencies();
    public String getPreferredCryptoCurrency();
    public BigDecimal getCryptoBalance(String cryptoCurrency);
}
