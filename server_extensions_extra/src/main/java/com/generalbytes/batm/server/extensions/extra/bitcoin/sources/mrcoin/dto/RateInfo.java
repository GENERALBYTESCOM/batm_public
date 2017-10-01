/*************************************************************************************
 * Copyright (C) 2014-2016 GENERAL BYTES s.r.o. All rights reserved.
 * Copyright (C) 2017 MrCoin Ltd.
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
package com.generalbytes.batm.server.extensions.extra.bitcoin.sources.mrcoin.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonSetter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@JsonIgnoreProperties({"ticker"})
public class RateInfo {

    private static final Logger log = LoggerFactory.getLogger(RateInfo.class);
    private Map<String, Ticker> atm_tickers;

    @JsonSetter("atm_ticker")
    public void setAtmTickers(Map<String, Ticker> atm_tickers) {
        this.atm_tickers = atm_tickers;
    }

    public Ticker getTicker(String fromCurrency, String toCurrency) {
        String pair = (fromCurrency + toCurrency).toUpperCase();
        return this.atm_tickers.get(pair);
    }

    public BigDecimal getRateBuy(String fromCurrency, String toCurrency) {
        Ticker t = getTicker(fromCurrency, toCurrency);
        return t.isValid() ? t.getAsk() : null;
    }

    public BigDecimal getRateSell(String fromCurrency, String toCurrency) {
        Ticker t = getTicker(fromCurrency, toCurrency);
        return t.isValid() ? t.getBid() : null;
    }

    public static class Ticker {
        private BigDecimal bid;
        private BigDecimal ask;
        private boolean valid;

        public Ticker(
                @JsonProperty("bid") BigDecimal bid,
                @JsonProperty("ask") BigDecimal ask,
                @JsonProperty("valid") boolean valid) {

            this.bid = bid;
            this.ask = ask;
            this.valid = valid;
        }

        public BigDecimal getBid() {
            return bid;
        }

        public BigDecimal getAsk() {
            return ask;
        }

        public boolean isValid() {
            return valid;
        }
    }
}
