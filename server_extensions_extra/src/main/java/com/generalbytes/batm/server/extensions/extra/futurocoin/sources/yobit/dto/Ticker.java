package com.generalbytes.batm.server.extensions.extra.futurocoin.sources.yobit.dto;

import java.math.BigDecimal;

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
}
