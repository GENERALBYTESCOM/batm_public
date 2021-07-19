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
import java.util.List;

public interface ILightningWallet extends IWalletAdvanced {

    /**
     * @param invoice
     * @param cryptoCurrency
     * @return Lightning Bitcoins received to this invoice
     */
    BigDecimal getReceivedAmount(String invoice, String cryptoCurrency);

    /**
     *
     *
     * @param cryptoAmount
     * @param cryptoCurrency
     * @param paymentValidityInSec expiry of generated invoice in seconds
     * @param description
     * @return
     */
    String getInvoice(BigDecimal cryptoAmount, String cryptoCurrency, Long paymentValidityInSec, String description);

    /**
     *
     * @return information about all channels opened by the node or to the node possibly including offline and closed ones
     */
    List<? extends ILightningChannel> getChannels();
}
