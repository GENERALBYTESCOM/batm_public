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

package com.generalbytes.batm.server.extensions;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.server.extensions.payment.ReceivedAmount;

import java.math.BigDecimal;
import java.util.List;

/**
 * Interface for interacting with a Lightning Bitcoin wallet.
 */
public interface ILightningWallet extends IWalletAdvanced {

    /**
     * <strong>WARNING:</strong>
     * <p>
     * This method was effectively replaced by {@link #getReceivedAmount(String)}.
     * As of now, this method is not directly used by the server and is kept for compatibility reasons.
     * <p>
     * Retrieves the amount of LBTC received for a specific invoice.
     *
     * @param invoice        the invoice identifier.
     * @param cryptoCurrency the cryptocurrency code (e.g., LBTC).
     * @return the amount of Lightning Bitcoins received for the invoice.
     * @deprecated use {@link #getReceivedAmount(String)} instead.
     */
    @Deprecated(since = "1.14")
    BigDecimal getReceivedAmount(String invoice, String cryptoCurrency);

    /**
     * Retrieves the amount of LBTC received for a specific invoice along with additional data as a {@link ReceivedAmount} object.
     *
     * @param invoice the invoice identifier.
     * @return the received amount information.
     */
    default ReceivedAmount getReceivedAmount(String invoice) {
        // Default implementation for compatibility reasons. Should be overridden in subclasses.
        BigDecimal amount = getReceivedAmount(invoice, CryptoCurrency.LBTC.getCode());
        if (amount == null) {
            return null;
        }
        return new ReceivedAmount(amount, 0);
    }

    /**
     * Generates a Lightning Bitcoin invoice.
     *
     * @param cryptoAmount         the amount of cryptocurrency to request.
     * @param cryptoCurrency       the cryptocurrency code (e.g., LBTC).
     * @param paymentValidityInSec the expiry time of the generated invoice in seconds.
     * @param description          a description of the invoice.
     * @return the serialized invoice string.
     */
    String getInvoice(BigDecimal cryptoAmount, String cryptoCurrency, Long paymentValidityInSec, String description);

    /**
     * Retrieves information about all channels opened by or to the node, possibly including offline and closed ones.
     *
     * @return a list of channels associated with the node.
     */
    List<? extends ILightningChannel> getChannels();

}
