/*************************************************************************************
 * Copyright (C) 2014-2016 GENERAL BYTES s.r.o. All rights reserved.
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
package com.generalbytes.batm.server.extensions.extra.gulden.sources;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public class GuldenTickerResponse {
    @JsonProperty("EUR")
    private EUR euro;
    @JsonProperty("BTC")
    private BTC btc;

    public class BTC {
        private String code;
        private String symbol;
        private BigDecimal buy;
        private BigDecimal sell;
        private BigDecimal buy15m;
        private BigDecimal sell15m;

        public String getCode() {
            return code;
        }
        public void setCode(String code) {
            this.code = code;
        }

        public String getSymbol() {
            return symbol;
        }
        public void setSymbol(String symbol) {
            this.symbol = symbol;
        }

        public BigDecimal getBuy() {
            return buy;
        }

        public void setBuy(BigDecimal buy) {
            this.buy = buy;
        }

        public BigDecimal getSell() {
            return sell;
        }

        public void setSell(BigDecimal sell) {
            this.sell = sell;
        }

        public BigDecimal getBuy15m() {
            return buy15m;
        }

        public void setBuy15m(BigDecimal buy15m) {
            this.buy15m = buy15m;
        }

        public BigDecimal getSell15m() {
            return sell15m;
        }

        public void setSell15m(BigDecimal sell15m) {
            this.sell15m = sell15m;
        }
    }

    public class EUR {
        private String code;
        private String symbol;
        private BigDecimal buy;
        private BigDecimal sell;
        private BigDecimal buy15m;
        private BigDecimal sell15m;

        public String getCode() {
            return code;
        }
        public void setCode(String code) {
            this.code = code;
        }

        public String getSymbol() {
            return symbol;
        }
        public void setSymbol(String symbol) {
            this.symbol = symbol;
        }

        public BigDecimal getBuy() {
            return buy;
        }

        public void setBuy(BigDecimal buy) {
            this.buy = buy;
        }

        public BigDecimal getSell() {
            return sell;
        }

        public void setSell(BigDecimal sell) {
            this.sell = sell;
        }

        public BigDecimal getBuy15m() {
            return buy15m;
        }

        public void setBuy15m(BigDecimal buy15m) {
            this.buy15m = buy15m;
        }

        public BigDecimal getSell15m() {
            return sell15m;
        }

        public void setSell15m(BigDecimal sell15m) {
            this.sell15m = sell15m;
        }
    }


	public EUR getEUR() {
		return euro;
	}
	public void setEUR(EUR euro) {
		this.euro = euro;
	}
	public BTC getBTC() {
		return btc;
	}
	public void setBTC(BTC btc) {
		this.btc = btc;
	}
}
