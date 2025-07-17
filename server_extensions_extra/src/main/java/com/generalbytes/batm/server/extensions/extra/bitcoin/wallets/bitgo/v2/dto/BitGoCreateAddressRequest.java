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
package com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.bitgo.v2.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

// the server did not like when we sent null gasPrice, the field has to be omitted instead
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class BitGoCreateAddressRequest {
    /**
     * Enum: 0 1 10 11 20 21
     * <p>
     * <a href="https://github.com/BitGo/unspents/blob/master/src/codes.ts">see GitHub</a>
     */
    private Integer chain;
    /**
     * <= 250 characters; A human-readable label which should be applied to the new address
     */
    private String label;
    /**
     * Whether the deployment of the address forwarder contract should use a low priority fee key (ETH only)
     */
    private Boolean lowPriority;
    /**
     * Explicit gas price to use when deploying the forwarder contract (ETH only).
     * If not given, defaults to the current estimated network gas price.
     */
    private String gasPrice;
}
