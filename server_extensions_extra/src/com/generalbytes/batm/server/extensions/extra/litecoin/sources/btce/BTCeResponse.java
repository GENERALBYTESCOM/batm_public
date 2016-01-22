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
package com.generalbytes.batm.server.extensions.extra.litecoin.sources.btce;

import java.math.BigDecimal;

public class BTCeResponse {
    private Ticker ticker;

    public class Ticker {
        private BigDecimal high;
        private BigDecimal low;
        private BigDecimal avg;
        private BigDecimal vol;
        private BigDecimal vol_cur;

        private BigDecimal last;
        private BigDecimal buy;
        private BigDecimal sell;

        private long updated;
        private long server_time;

        public BigDecimal getHigh() {
            return high;
        }

        public void setHigh(BigDecimal high) {
            this.high = high;
        }

        public BigDecimal getLow() {
            return low;
        }

        public void setLow(BigDecimal low) {
            this.low = low;
        }

        public BigDecimal getAvg() {
            return avg;
        }

        public void setAvg(BigDecimal avg) {
            this.avg = avg;
        }

        public BigDecimal getVol() {
            return vol;
        }

        public void setVol(BigDecimal vol) {
            this.vol = vol;
        }

        public BigDecimal getVol_cur() {
            return vol_cur;
        }

        public void setVol_cur(BigDecimal vol_cur) {
            this.vol_cur = vol_cur;
        }

        public BigDecimal getLast() {
            return last;
        }

        public void setLast(BigDecimal last) {
            this.last = last;
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

        public long getUpdated() {
            return updated;
        }

        public void setUpdated(long updated) {
            this.updated = updated;
        }

        public long getServer_time() {
            return server_time;
        }

        public void setServer_time(long server_time) {
            this.server_time = server_time;
        }
    }

    public Ticker getTicker() {
        return ticker;
    }

    public void setTicker(Ticker ticker) {
        this.ticker = ticker;
    }
}
