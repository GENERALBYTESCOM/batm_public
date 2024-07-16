/*************************************************************************************
 * Copyright (C) 2014-2024 GENERAL BYTES s.r.o. All rights reserved.
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
package com.generalbytes.batm.server.extensions.watchlist;

import java.io.Serializable;

public class WatchListScanIdentityMatchesData implements Serializable {

    /**
     * Public ID of identity.
     */
    private String identityPublicId;

    /**
     * Code of WatchList.
     */
    private String watchListCode;

    /**
     * Unique identifier of the matching entity.
     */
    private String partyId;

    /**
     * Type of WatchList trigger.
     */
    private WatchListTrigger trigger;

    /**
     * Result of scan.
     */
    private WatchListScanResult scanResult;

    /**
     * Terminal serial number. It is available if the trigger is {@link WatchListTrigger#PRE_TRANSACTION}, otherwise null.
     */
    private String terminalSerialNumber;

    public String getIdentityPublicId() {
        return identityPublicId;
    }

    public void setIdentityPublicId(String identityPublicId) {
        this.identityPublicId = identityPublicId;
    }

    public String getWatchListCode() {
        return watchListCode;
    }

    public void setWatchListCode(String watchListCode) {
        this.watchListCode = watchListCode;
    }

    public String getPartyId() {
        return partyId;
    }

    public void setPartyId(String partyId) {
        this.partyId = partyId;
    }

    public WatchListTrigger getTrigger() {
        return trigger;
    }

    public void setTrigger(WatchListTrigger trigger) {
        this.trigger = trigger;
    }

    public WatchListScanResult getScanResult() {
        return scanResult;
    }

    public void setScanResult(WatchListScanResult scanResult) {
        this.scanResult = scanResult;
    }

    public String getTerminalSerialNumber() {
        return terminalSerialNumber;
    }

    public void setTerminalSerialNumber(String terminalSerialNumber) {
        this.terminalSerialNumber = terminalSerialNumber;
    }

}
