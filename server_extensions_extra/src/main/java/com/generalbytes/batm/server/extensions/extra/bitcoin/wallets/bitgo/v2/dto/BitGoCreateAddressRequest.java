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

public class BitGoCreateAddressRequest {
    private Integer chain;
    private String label;
    private Boolean lowPriority;
    private String gasPrice;

    public Integer getChain() {
        return chain;
    }

    /**
     * Enum:0 1 10 11 20 21
     * https://github.com/BitGo/unspents/blob/master/src/codes.ts
     */
    public void setChain(Integer chain) {
        this.chain = chain;
    }

    public String getLabel() {
        return label;
    }

    /**
     * <= 250 characters; A human-readable label which should be applied to the new address
     */
    public void setLabel(String label) {
        this.label = label;
    }

    public Boolean getLowPriority() {
        return lowPriority;
    }

    /**
     * Whether the deployment of the address forwarder contract should use a low priority fee key (ETH only)
     */
    public void setLowPriority(Boolean lowPriority) {
        this.lowPriority = lowPriority;
    }

    public String getGasPrice() {
        return gasPrice;
    }

    /**
     * Explicit gas price to use when deploying the forwarder contract (ETH only). If not given, defaults to the current estimated network gas price.
     */
    public void setGasPrice(String gasPrice) {
        this.gasPrice = gasPrice;
    }
}
