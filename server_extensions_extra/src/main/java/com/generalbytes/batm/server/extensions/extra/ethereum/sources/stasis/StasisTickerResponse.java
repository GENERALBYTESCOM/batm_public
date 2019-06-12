package com.generalbytes.batm.server.extensions.extra.ethereum.sources.stasis;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;

public class StasisTickerResponse {
    @JsonProperty("rates")
    private HashMap<String, StasisRateDescription> rates;

    public HashMap<String, StasisRateDescription> getRates() {
        return rates;
    }

    public void setRates(HashMap<String, StasisRateDescription> rates) {
        this.rates = rates;
    }

    public StasisRateDescription getEUR() {
        return rates.get("EUR:EURS");
    }

    public StasisRateDescription getEURS() {
        return rates.get("EURS:EUR");
    }

}
