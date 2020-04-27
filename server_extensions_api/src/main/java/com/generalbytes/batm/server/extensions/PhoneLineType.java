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

import java.io.Serializable;

@SuppressWarnings("WeakerAccess")
public class PhoneLineType implements Serializable {

    public static final int LEVEL_LOW           = 0;
    public static final int LEVEL_MEDIUM_LOW    = 1;
    public static final int LEVEL_MEDIUM_HIGH   = 2;
    public static final int LEVEL_HIGH          = 3;

    public static final int ACTION_ALLOW        = 0;
    public static final int ACTION_FLAG         = 1;
    public static final int ACTION_BLOCK        = 2;

    private String error;
    private PhoneLineTypeCode phoneTypeCode;

    public enum PhoneLineTypeCode {

        FIXED_LINE(1,
            LEVEL_LOW,
            ACTION_ALLOW),

        MOBILE(2,
            LEVEL_MEDIUM_LOW,
            ACTION_ALLOW),

        PREPAID(3,
            LEVEL_MEDIUM_HIGH,
            ACTION_FLAG),

        TOLL_FREE(4,
            LEVEL_HIGH,
            ACTION_BLOCK),

        VOIP(5,
            LEVEL_HIGH,
            ACTION_BLOCK),

        PAGER(6,
            LEVEL_HIGH,
            ACTION_BLOCK),

        PAYPHONE(7,
            LEVEL_HIGH,
            ACTION_BLOCK),

        INVALID(8,
            LEVEL_HIGH,
            ACTION_BLOCK),

        RESTRICTED_PREMIUM(9,
            LEVEL_HIGH,
            ACTION_BLOCK),

        PERSONAL(10,
            LEVEL_MEDIUM_LOW,
            ACTION_ALLOW),

        VOICEMAIL(11,
            LEVEL_MEDIUM_HIGH,
            ACTION_BLOCK),

        OTHER(20,
            LEVEL_MEDIUM_HIGH,
            ACTION_BLOCK);

        private int code;
        private int risk;
        private int action;

        PhoneLineTypeCode(int code, int risk, int action) {
            this.code = code;
            this.risk = risk;
            this.action = action;
        }

        public static PhoneLineTypeCode getInstanceByTelesign(int code) {
            for (PhoneLineTypeCode phoneTypeCode : PhoneLineTypeCode.values()) {
                if (phoneTypeCode.code == code) {
                    return phoneTypeCode;
                }
            }
            return null;
        }

        public int getCode() {
            return code;
        }

        public String getCodeAsString() {
            return "" + code;
        }

        public int getRisk() {
            return risk;
        }

        public int getAction() {
            return action;
        }

        @Override
        public String toString() {
            return "PhoneTypeCode{" + code + "/" + name() + '}';
        }
    }

    public PhoneLineTypeCode getPhoneTypeCode() {
        return phoneTypeCode;
    }

    public void setPhoneTypeCode(PhoneLineTypeCode phoneTypeCode) {
        this.phoneTypeCode = phoneTypeCode;
    }

    /**
     * Error message. Contact data are not sent.
     */
    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    @Override
    public String toString() {
        if (error != null) {
            return "PhoneType{error='" + error + '}';
        }
        return "PhoneType{code='" + phoneTypeCode + "'}";
    }
}
