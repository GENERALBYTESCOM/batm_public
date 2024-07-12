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

public class WatchListScanIdentityMatchesData {

    /**
     * Public ID of identity. Can be null.
     */
    private final String identityPublicId;
    /**
     * Message body.
     */
    private final String messageBody;
    /**
     * Type of WatchList trigger.
     */
    private final WatchListTrigger trigger;
    /**
     * Terminal serial number. Can be null.
     */
    private final String terminalSerialNumber;
    /**
     * Match score by WatchList provider. Can be null.
     */
    private final Integer matchScore;
    /**
     * Result of scan.
     */
    private final WatchListScanResult scanResult;

    public WatchListScanIdentityMatchesData(String identityPublicId,
                                            String messageBody,
                                            WatchListTrigger trigger,
                                            String terminalSerialNumber,
                                            Integer matchScore,
                                            WatchListScanResult scanResult
    ) {
        this.identityPublicId = identityPublicId;
        this.messageBody = messageBody;
        this.trigger = trigger;
        this.terminalSerialNumber = terminalSerialNumber;
        this.matchScore = matchScore;
        this.scanResult = scanResult;
    }

    public String getIdentityPublicId() {
        return identityPublicId;
    }

    public String getMessageBody() {
        return messageBody;
    }

    public WatchListTrigger getTrigger() {
        return trigger;
    }

    public String getTerminalSerialNumber() {
        return terminalSerialNumber;
    }

    public Integer getMatchScore() {
        return matchScore;
    }

    public WatchListScanResult getScanResult() {
        return scanResult;
    }

}
