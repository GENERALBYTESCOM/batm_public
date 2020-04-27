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

public class CBTransaction {

    public String id;
    public String type;
    public String status;
    public CBAmount amount;
    public CBAmount native_amount;
    public String description;
    public String created_at;
    public String updated_at;
    public String resource;
    public String resource_path;
    public CBNetwork network;
    public CBTo to;
    public CBDetails details;

    public static class CBNetwork {
        public String status;
        public String hash;
        public String name;
    }

    public static class CBTo {
        public String resource;
        public String address;
    }

    public static class CBDetails {
        public String title;
        public String subtitle;
    }
}
