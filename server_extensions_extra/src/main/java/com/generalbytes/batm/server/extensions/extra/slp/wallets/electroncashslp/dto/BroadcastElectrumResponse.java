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
package com.generalbytes.batm.server.extensions.extra.slp.wallets.electroncashslp.dto;

import java.util.List;

public class BroadcastElectrumResponse extends ElectrumResponse {
    public List<Object> result;
    /*
    [ false, "error: The transaction already exists in the blockchain." ]
    [ true, "59851948f7d27f4b17c122f63c68a0b0388b023bc61edd1f5f44b1a22e154bed" ]
     */

    public boolean isSuccess() {
        return result != null && result.size() == 2 && result.get(0) instanceof Boolean && (Boolean) result.get(0) && result.get(1) instanceof String;
    }

    public String getTxId() {
        if (isSuccess()) {
            return (String) result.get(1);
        }
        return null;
    }

    public String getError() {
        if (!isSuccess() && result.get(1) instanceof String) {
            return (String) result.get(1);
        }
        return null;
    }
}
