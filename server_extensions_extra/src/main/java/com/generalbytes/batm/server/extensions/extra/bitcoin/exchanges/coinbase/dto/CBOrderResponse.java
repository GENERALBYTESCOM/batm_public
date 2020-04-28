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
package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.coinbase.dto;

public class CBOrderResponse extends CBResponse {

    public CBOrder data;

    public static class CBOrder {

        public String id;
        public String status;
        public CBResource payment_method;
        public CBResource transaction;
        public CBAmount amount;
        public CBAmount total;
        public CBAmount subtotal;
        public String created_at;
        public String updated_at;
        public String resource;
        public String resource_path;
        public boolean committed;
        public boolean instant;
        public CBAmount fee;
        public String payout_at;
    }
}
