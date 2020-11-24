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


import java.math.BigDecimal;

public interface IBanknoteCounts {
    String CN_ACCEPTOR_CASHBOX = "acceptor_cashbox";
    String CN_DISPENSER_CASSETTE_1 = "dispenser_cassette_1";
    String CN_DISPENSER_CASSETTE_2 = "dispenser_cassette_2";
    String CN_DISPENSER_CASSETTE_3 = "dispenser_cassette_3";
    String CN_DISPENSER_CASSETTE_4 = "dispenser_cassette_4";
    String CN_DISPENSER_CASSETTE_5 = "dispenser_cassette_5";
    String CN_DISPENSER_CASSETTE_6 = "dispenser_cassette_6";
    String CN_DISPENSER_REJECT = "dispenser_reject";
    String CN_RECYCLER_DRUMS = "recycler_drums";
    String CN_RECYCLER_MISSING = "recycler_missing";
    String CN_RECYCLER_UNKNOWN = "recycler_unknown";

    String getCashboxName();
    BigDecimal getDenomination();
    String getCurrency();
    int getCount();
    Integer getCapacity();
}
