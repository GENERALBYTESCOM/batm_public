package com.generalbytes.batm.server.extensions.extra.dogecoin.wallets.blockio.dto;

public class BlockIOResponseSubmitTransaction {
    private String status;
    private BlockIOData data;

    public static class BlockIOData {
        private String network;
        private String txid;

        public String getNetwork() {
            return network;
        }

        public void setNetwork(String network) {
            this.network = network;
        }

        public String getTxid() {
            return txid;
        }

        public void setTxid(String txid) {
            this.txid = txid;
        }
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BlockIOData getData() {
        return data;
    }

    public void setData(BlockIOData data) {
        this.data = data;
    }
}
