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

public class Converters {

    public static final BigDecimal BCH = BigDecimal.valueOf(Math.pow(10, 8));
    public static final BigDecimal BTC = BigDecimal.valueOf(Math.pow(10, 8));
    public static final BigDecimal LTC = BigDecimal.valueOf(Math.pow(10, 8));
    public static final BigDecimal ETH = BigDecimal.valueOf(Math.pow(10, 18));
    public static final BigDecimal BTBS = BigDecimal.valueOf(Math.pow(10, 18));
    public static final BigDecimal USDT = BigDecimal.valueOf(Math.pow(10, 6));

    public static final BigDecimal TBCH = BigDecimal.valueOf(Math.pow(10, 8));
    public static final BigDecimal TBTC = BigDecimal.valueOf(Math.pow(10, 8));
    public static final BigDecimal TLTC = BigDecimal.valueOf(Math.pow(10, 8));
}
