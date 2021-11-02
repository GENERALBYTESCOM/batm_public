/*************************************************************************************
 * Copyright (C) 2014-2021 GENERAL BYTES s.r.o. All rights reserved.
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

public interface IOpeningHours {

    /**
     *  Day of the week
     */
    OpeningDay getDay();

    /**
     * Opening time
     */
    Date getFrom();

    /**
     * Closing time
     */
    Date getTo();

    /**
     * True if the day is scheduled for a cash withdrawal.
     */
    boolean isCashCollectionDay();

    enum OpeningDay {
        /**
         * Used for the first version of the opening hours.
         * Opening hours are not defined for each day separately.
         */
        EVERY,

        MON,
        TUE,
        WED,
        THU,
        FRI,
        SAT,
        SUN
    }

}
