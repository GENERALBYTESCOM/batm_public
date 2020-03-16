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
package com.generalbytes.batm.server.extensions.extra.slp.wallets.electroncashslp.dto;

import java.util.ArrayList;
import java.util.List;

public class ListAddressesAndBalancesElectrumResponse extends ElectrumResponse {
    // [["qqe6k5r39trndl0zvqv8rpc79urnujmljyug0x5v4v", "0,"], ["qqkr9pyxkewrjnnuzdlz8a0kxy2uggcgavl6p8fn3k", "0,00000546"]]
    public List<AddressInfoResult> result;

    public static class AddressInfoResult extends ArrayList<String> {

        public String getAddress() {
            return get(0);
        }

        /**
         * @return balance in a broken format, e.g. "0," for zero.
         */
        public String getBalance() {
            return get(1);
        }
    }
}
