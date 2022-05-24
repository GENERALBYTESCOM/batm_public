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
import java.util.Map;

/**
 * Contains information about an event triggering a notification,
 * for example "A transaction for USD 100 failed on terminal BT1XXXXX".
 */
public interface INotificationDetails {

    /**
     * @return serial number of the terminal where the notification originated
     */
    String getTerminalSerialNumber();

    /**
     * @return fiat or crypto amount parameter of the notification,
     * e.g. transaction fiat amount for CONDITION_TRANSACTION_SUCCESS / FAILED,
     * wallet crypto balance for CONDITION_WALLET_CRYPTO_BALANCE_LOW / HIGH
     * or exchange fiat balance for CONDITION_EXCHANGE_FIAT_BALANCE_LOW / HIGH
     * or it could be null.
     */
    BigDecimal getAmount();

    /**
     * @return unit of the {@link #getAmount()} value, e.g. "BTC" or "USD"
     */
    String getUnit();

    /**
     * @return Additional data for some notifications, depends on the notification type.
     * For example <code>notificationDetails.getAdditionalData().get("cryptoAddress")</code> for CONDITION_BLACKLISTED_ADDRESS_USED
     * or <code>notificationDetails.getAdditionalData().get("details").get("identityPublicId")</code>
     * for CONDITION_TRANSACTION_CASH_LIMIT_REACHED, etc.
     */
    Map<String, ?> getAdditionalData();
}
