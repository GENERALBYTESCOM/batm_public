/* ##
# Part of the Aquanow API price extension
#
# Copyright 2018 dustinface
# Created 29.04.2018
#
* This software may be distributed and modified under the terms of the GNU
* General Public License version 2 (GPL2) as published by the Free Software
* Foundation and appearing in the file GPL2.TXT included in the packaging of
* this file. Please note that GPL2 Section 2[b] requires that all works based
* on this software must also be made publicly available under the terms of
* the GPL2 ("Copyleft").
#
## */

package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.aqua.dto;

import java.math.BigDecimal;
import java.lang.reflect.Field;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.*;


public class RateResponse {

    public BigDecimal count;
    public Item[] items;
    public Last last;
    public String resource;
    public BigDecimal status;
    public BigDecimal execution;
    @JsonProperty("dataType")
    public String dataType;
    @JsonProperty("symbol")
    public String symbol;
    @JsonProperty("bestBid")
    public BigDecimal bestBid;
    @JsonProperty("bestAsk")
    public BigDecimal bestAsk;
    @JsonProperty("lastUpdated")
    public BigDecimal lastUpdated;
    @JsonProperty("spread")
    public BigDecimal spread;
    @JsonProperty("precisions")
    public List<Integer> precisions = new ArrayList<Integer>(5);

    public RateResponse(){
        super();
    }

    public RateResponse(String dataType, BigDecimal bestBid, BigDecimal bestAsk){
        this.dataType = dataType;
        this.bestBid = bestBid;
        this.bestAsk = bestAsk;

    }

    public BigDecimal getBestBid() {
        return bestBid;
    }
        public BigDecimal getBestAsk() {
        return bestAsk;
    }
    
    public static class Item {
        public String updated;
        public Currency currencies;
    }

    public static class Currency {
        public BigDecimal USD;
        public BigDecimal CAD;
    }

    public class Last {
        public String id;
        public String created;
    }


    public BigDecimal getPrice(String fiatCurrency) {

        BigDecimal price = null;
        return price;
    }
}
