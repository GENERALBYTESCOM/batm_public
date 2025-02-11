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
package com.generalbytes.batm.server.extensions.travelrule;

import java.math.BigDecimal;

/**
 * Represents the data required to create a new Travel Rule transfer and send it to a beneficiary VASP
 * or receive one from an originator VASP.
 */
public interface ITravelRuleTransferData {

    /**
     * Get the unique identifier for this transfer, generated on the server side.
     *
     * <p>This identifier serves as a critical reference for this transfer in server-side operations.
     * It is essential for tracking, managing, and identifying the transfer in the system.</p>
     */
    String getPublicId();

    /**
     * Get data about the originator involved in this transfer.
     *
     * @return An {@link ITravelRuleNaturalPerson} object representing the originator.
     */
    ITravelRuleNaturalPerson getOriginator();

    /**
     * Get data about the beneficiary involved in this transfer.
     *
     * @return An {@link ITravelRuleNaturalPerson} object representing the beneficiary.
     */
    ITravelRuleNaturalPerson getBeneficiary();

    /**
     * Get data about the originator's VASP.
     *
     * @return An {@link ITravelRuleVasp} object representing the originator's VASP.
     */
    ITravelRuleVasp getOriginatorVasp();

    /**
     * Get data about the beneficiary's VASP.
     *
     * @return An {@link ITravelRuleVasp} object representing the beneficiary's VASP.
     */
    ITravelRuleVasp getBeneficiaryVasp();

    /**
     * Get the cryptocurrency used in the transaction.
     *
     * <p>The cryptocurrency is specified as a ticker symbol, e.g., "BTC" for Bitcoin.</p>
     */
    String getTransactionAsset();

    /**
     * Get the amount of the cryptocurrency asset being transferred.
     *
     * <p>The amount is expressed in base units of the asset.</p>
     */
    long getTransactionAmount();

    /**
     * Get the destination crypto address.
     *
     * <p>This address specifies where the asset is being sent.</p>
     */
    String getDestinationAddress();

    /**
     * Get the amount in a fiat currency.
     */
    BigDecimal getFiatAmount();

    /**
     * Get the fiat currency used.
     *
     * <p>This specifies the currency in which the fiat amount is denominated,
     * represented as a currency code (e.g., "USD" for US Dollars).</p>
     */
    String getFiatCurrency();

    /**
     * Get the blockchain transaction hash.
     *
     * @return The transaction hash or null if it is not known yet.
     */
    String getTransactionHash();

}
