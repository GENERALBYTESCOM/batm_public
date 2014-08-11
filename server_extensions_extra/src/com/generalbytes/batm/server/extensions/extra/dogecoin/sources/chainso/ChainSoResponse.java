package com.generalbytes.batm.server.extensions.extra.dogecoin.sources.chainso;

/**
 * Created by b00lean on 8/11/14.
 */
public class ChainSoResponse {
    private String status;
    private Data data;

    public class Data {
        private String network;
        private ChainSoPrice[] prices;

        public String getNetwork() {
            return network;
        }

        public void setNetwork(String network) {
            this.network = network;
        }

        public ChainSoPrice[] getPrices() {
            return prices;
        }

        public void setPrices(ChainSoPrice[] prices) {
            this.prices = prices;
        }
    }



    public String getStatus() {
        return status;
    }

    public Data getData() {
        return data;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setData(Data data) {
        this.data = data;
    }
}
