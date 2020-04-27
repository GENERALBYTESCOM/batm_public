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

public class PhoneNumberQueryResult {
    private final boolean querySuccessful;
    private final PhoneLineType phoneLineType;
    private final boolean lineTypeBlocked;

    public PhoneNumberQueryResult(boolean querySuccessful, PhoneLineType phoneLineType, boolean lineTypeBlocked) {
        this.querySuccessful = querySuccessful;
        this.phoneLineType = phoneLineType;
        this.lineTypeBlocked = lineTypeBlocked;
    }

    /**
     *
     * @return false if querying is not configured properly or it failed.
     */
    public boolean isQuerySuccessful() {
        return querySuccessful;
    }

    public PhoneLineType getPhoneLineType() {
        return phoneLineType;
    }

    public boolean isLineTypeBlocked() {
        return lineTypeBlocked;
    }
}
