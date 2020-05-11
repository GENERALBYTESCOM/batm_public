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

import java.util.Collection;
import java.util.Date;

public interface ITerminalCashCollectionRecord {

    /**
     * Returns serial number of a terminal
     * @return
     */
    String getTerminalSerialNumber();
    /**
     * Timestamp when event was created on terminal
     * @return
     */
    Date getTerminalTime();

    /**
     * Timestamp when event was delivered and stored on server
     * @return
     */
    Date getServerTime();

    /**
     * Total amounts in cashbox per fiat currency. If your ATM sells BTC for USD only then you get only one member in the list.
     * @return
     */
    Collection<IAmount> getAmounts();

    /**
     * Reserved for the future, not currently used - the person that performed cash collection
     * @return
     */
    IPerson getCollectingPerson();

    /**
     * Complicated string containing description on what was in the cashbox when cash collecting
     * @return
     */
    String getContains();

    /**
     * Text description that can be set by user via admin
     * @return
     */
    String getNote();

    /**
     * Returns value of long counter at the time of cash collection
     * @return
     */
    String getCountersLong();

    /**
     * Returns value of short counter at the time of cash collection before it was set to 0.
     * @return
     */
    String getCountersShort();

    /**
     * Returns name of the cashbox
     * @return
     */
    String getCashboxName();
}
