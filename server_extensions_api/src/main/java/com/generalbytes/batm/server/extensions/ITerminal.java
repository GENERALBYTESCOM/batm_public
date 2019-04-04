/*************************************************************************************
 * Copyright (C) 2014-2019 GENERAL BYTES s.r.o. All rights reserved.
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

import java.util.Date;
import java.util.List;

public interface ITerminal {
    int TYPE_PHYSICAL = 0;
    int TYPE_VIRTUAL = 1;

    Integer getType();
    String getSerialNumber();
    String getName();
    boolean isActive();
    ILocation getLocation();
    Date getConnectedAt();
    Date getLastPingAt();
    long getLastPingDuration();

    Date getExchangeRateUpdatedAt();
    String getExchangeRatesBuy();
    String getExchangeRatesSell();

    long getErrors();
    int getOperationalMode();
    int getRejectedReason();

    List<String> getAllowedCashCurrencies();
    List<String> getAllowedCryptoCurrencies();
}
