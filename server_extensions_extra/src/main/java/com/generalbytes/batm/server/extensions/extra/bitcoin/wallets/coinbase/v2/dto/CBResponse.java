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
package com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.coinbase.v2.dto;

import java.util.List;

/**
 * Created by b00lean on 23.7.17.
 */

public class CBResponse {
    private List<CBError> errors;
    private List<CBWarning> warnings;

    public List<CBError> getErrors() {
        return errors;
    }

    public void setErrors(List<CBError> errors) {
        this.errors = errors;
    }

    public List<CBWarning> getWarnings() {
        return warnings;
    }

    public void setWarnings(List<CBWarning> warnings) {
        this.warnings = warnings;
    }

    public String getErrorMessages() {
        return toString(errors);
    }

    private String toString(List<CBError> errors) {
        if (errors != null && errors.size() > 0) {
            StringBuilder sb = new StringBuilder();
            for (CBError v : errors) {
                if (v.getId() != null) {
                    sb.append(sb.length() == 0 ? "" : ", ");
                    sb.append("id = ").append(v.getId());
                }
                if (v.getMessage() != null) {
                    sb.append(sb.length() == 0 ? "" : ", ");
                    sb.append("message = ").append(v.getMessage());
                }
            }
            return sb.toString();
        }
        return null;
    }

}
