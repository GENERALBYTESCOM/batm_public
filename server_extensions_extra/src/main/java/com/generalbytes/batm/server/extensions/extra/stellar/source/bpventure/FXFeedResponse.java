package com.generalbytes.batm.server.extensions.extra.stellar.source.bpventure;

import java.util.Map;

public class FXFeedResponse {
    private Data data;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public static class Data {
        private String currency;
        private Map<String, String> rates;

        public String getCurrency() {
            return currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }

        public Map<String, String> getRates() {
            return rates;
        }

        public void setRates(Map<String, String> rates) {
            this.rates = rates;
        }
    }
}
