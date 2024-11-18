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
package com.generalbytes.batm.server.extensions;

import java.util.Date;

/**
 * Information about how a transaction should be inserted into an Input Queue.
 * This is initially configured in admin per Input Queue, and it can be overridden by extensions for each transaction.
 * An extension can decide to skip the queue that is configured in admin (do not add the transaction to it and send it immediately)
 * but it cannot request to queue a transaction if no Input Queue is configured in admin.
 */
public class InputQueueInsertConfig {
    private boolean skipQueue = false;
    private boolean insertIntoSecondaryQueue = false;

    private boolean manualApprovalRequired = false;
    private int delaySeconds = 0;

    private boolean secondaryManualApprovalRequired = false;
    private int secondaryDelaySeconds = 0;
    private Date unlockTime;

    public boolean isSkipQueue() {
        return skipQueue;
    }

    public void setSkipQueue(boolean skipQueue) {
        this.skipQueue = skipQueue;
    }

    public boolean isInsertIntoSecondaryQueue() {
        return insertIntoSecondaryQueue;
    }

    public void setInsertIntoSecondaryQueue(boolean insertIntoSecondaryQueue) {
        this.insertIntoSecondaryQueue = insertIntoSecondaryQueue;
    }

    public boolean isManualApprovalRequired() {
        return manualApprovalRequired;
    }

    public void setManualApprovalRequired(boolean manualApprovalRequired) {
        this.manualApprovalRequired = manualApprovalRequired;
    }

    public int getDelaySeconds() {
        return delaySeconds;
    }

    public void setDelaySeconds(int delaySeconds) {
        this.delaySeconds = delaySeconds;
    }

    public boolean isSecondaryManualApprovalRequired() {
        return secondaryManualApprovalRequired;
    }

    public void setSecondaryManualApprovalRequired(boolean secondaryManualApprovalRequired) {
        this.secondaryManualApprovalRequired = secondaryManualApprovalRequired;
    }

    public int getSecondaryDelaySeconds() {
        return secondaryDelaySeconds;
    }

    public void setSecondaryDelaySeconds(int secondaryDelaySeconds) {
        this.secondaryDelaySeconds = secondaryDelaySeconds;
    }

    public Date getUnlockTime() {
        return unlockTime;
    }

    public void setUnlockTime(Date unlockTime) {
        this.unlockTime = unlockTime;
    }

    @Override
    public String toString() {
        return "InputQueueInsertConfig{" +
               "skipQueue=" + skipQueue +
               ", manualApprovalRequired=" + manualApprovalRequired +
               ", delaySeconds=" + delaySeconds +
               ", insertIntoSecondaryQueue=" + insertIntoSecondaryQueue +
               ", secondaryManualApprovalRequired=" + secondaryManualApprovalRequired +
               ", secondaryDelaySeconds=" + secondaryDelaySeconds +
               ", unlockTime=" + unlockTime +
               '}';
    }
}
