package com.generalbytes.batm.server.extensions.extra.potcoin.sources.coinmarketcap;

import java.math.BigDecimal;

public class CoinmarketcapResponse {

    private BigDecimal price_usd;
    private BigDecimal price_cad;
    private BigDecimal price_eur;

    public BigDecimal getPrice_usd() {
        return price_usd;
    }

    public void setPrice_usd(BigDecimal price_usd) {
        this.price_usd = price_usd;
    }

    public BigDecimal getPrice_cad() {
        return price_cad;
    }

    public void setPrice_cad(BigDecimal price_cad) {
        this.price_cad = price_cad;
    }

    public BigDecimal getPrice_eur() {
        return price_eur;
    }

    public void setPrice_eur(BigDecimal price_eur) {
        this.price_eur = price_eur;
    }
}
