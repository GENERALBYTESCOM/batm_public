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
package com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.coinbase.v2.dto;

/**
 * Created by b00lean on 23.7.17.
 */

public class CBPagination {
    private String ending_before;
    private String starting_after;
    private String order;
    private long limit;
    private String previous_uri;
    private String next_uri;

    public String getEnding_before() {
        return ending_before;
    }

    public void setEnding_before(String ending_before) {
        this.ending_before = ending_before;
    }

    public String getStarting_after() {
        return starting_after;
    }

    public void setStarting_after(String starting_after) {
        this.starting_after = starting_after;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public long getLimit() {
        return limit;
    }

    public void setLimit(long limit) {
        this.limit = limit;
    }

    public String getPrevious_uri() {
        return previous_uri;
    }

    public void setPrevious_uri(String previous_uri) {
        this.previous_uri = previous_uri;
    }

    public String getNext_uri() {
        return next_uri;
    }

    public void setNext_uri(String next_uri) {
        this.next_uri = next_uri;
    }
}
