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
package com.generalbytes.batm.server.extensions.aml.scoring;

import java.io.Serializable;

public final class ScoringResult implements Serializable {
    private final Integer risk;
    private final boolean highRisk;
    private boolean suspicious;
    private final String message;
    private boolean scoringPerformed;

    public ScoringResult(Integer risk, boolean highRisk, boolean suspicious, String message, boolean scoringPerformed) {
        this.risk = risk;
        this.highRisk = highRisk;
        this.suspicious = suspicious;
        this.message = message;
        this.scoringPerformed = scoringPerformed;
    }

    /**
     * @return Message or a reason for flagging the address or transaction provided by the scoring provider.
     */
    public String getMessage() {
        return message;
    }

    /**
     * @return null if no score was given or scoring failed. Value 0 to 10 otherwise. Higher means more risk.
     */


    public Integer getScore() {
        return risk;
    }

    /**
     * @return true if the transaction should be declined. False if transaction is not flagged
     * by the scoring provider or scoring was not performed.
     */


    public boolean isHighRisk() {
        return highRisk;
    }

    /**
     *
     * @return true if the transaction is scored with a high risk score but not necessarily high enough to be declined.
     *  False otherwise or if scoring was not performed.
     *  Every "High risk" transaction is suspicious, not every "suspicious" one is "high risk".
     *  If transaction is "Suspicious" but not "high risk" a notification is generated but the transaction is not blocked
     */
    public boolean isSuspicious() {
        return suspicious;
    }

    /**
     * @return true if scoring was performed with a positive or negative result.
     * False if scoring failed, an error occurred, or the provider does not support
     * scoring of the provided transaction, address or cryptocurrency.
     */

    public boolean isScoringPerformed() {
        return scoringPerformed;
    }

    @Override
    public String toString() {
        return "ScoringResult{" +
            "scoringPerformed=" + scoringPerformed +
            ", risk=" + risk +
            ", highRisk=" + highRisk +
            ", message='" + message + '\'' +
            '}';
    }

    /**
     * can be used when scoring failed, is disabled or not configured properly.
     */
    public static ScoringResult notPerformed(String message) {
        return new ScoringResult(null, false, false, message, false);
    }

    public static ScoringResult notPerformed() {
        return notPerformed(null);
    }
}
