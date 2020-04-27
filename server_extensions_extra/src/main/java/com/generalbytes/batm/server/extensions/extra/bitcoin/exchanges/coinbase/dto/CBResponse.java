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
package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.coinbase.dto;

public class CBResponse {

    public CBError[] errors;
    @SuppressWarnings("WeakerAccess")
    public CBError[] warnings;

    public static class CBError {
        public String id;
        public String message;
        public String url;

        @Override
        public String toString() {
            return "CBError{" +
                "id='" + id + '\'' +
                ", message='" + message + '\'' +
                ", url='" + url + '\'' +
                '}';
        }
    }

    public String getErrorMessages() {
        return toString(errors);
    }

    @SuppressWarnings("unused")
    public String getWarningMessages() {
        return toString(warnings);
    }

    private String toString(CBError[] values) {
        if (values != null && values.length > 0) {
            StringBuilder sb = new StringBuilder();
            for (CBError v : values) {
                if (v.id != null) {
                    sb.append(sb.length() == 0 ? "" : ", ");
                    sb.append("id = ").append(v.id);
                }
                if (v.message != null) {
                    sb.append(sb.length() == 0 ? "" : ", ");
                    sb.append("message = ").append(v.message);
                }
                if (v.url != null) {
                    sb.append(sb.length() == 0 ? "" : ", ");
                    sb.append("url = ").append(v.url);
                }
            }
            return sb.toString();
        }
        return null;
    }

}
