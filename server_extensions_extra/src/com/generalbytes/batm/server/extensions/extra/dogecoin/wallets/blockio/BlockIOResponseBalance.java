package com.generalbytes.batm.server.extensions.extra.dogecoin.wallets.blockio;

/**
 * Created by b00lean on 8/11/14.
 */
public class BlockIOResponseBalance {
    private String status;
    private BlockIOData data;

    public BlockIOResponseBalance() {
    }

    public class BlockIOData {
        private String network;
        private String available_balance;
        private String unconfirmed_sent_balance;
        private String unconfirmed_received_balance;
        private String pending_received_balance;


        public BlockIOData() {
        }

        public String getNetwork() {
            return network;
        }

        public void setNetwork(String network) {
            this.network = network;
        }

        public String getAvailable_balance() {
            return available_balance;
        }

        public void setAvailable_balance(String available_balance) {
            this.available_balance = available_balance;
        }

        public String getUnconfirmed_sent_balance() {
            return unconfirmed_sent_balance;
        }

        public void setUnconfirmed_sent_balance(String unconfirmed_sent_balance) {
            this.unconfirmed_sent_balance = unconfirmed_sent_balance;
        }

        public String getUnconfirmed_received_balance() {
            return unconfirmed_received_balance;
        }

        public void setUnconfirmed_received_balance(String unconfirmed_received_balance) {
            this.unconfirmed_received_balance = unconfirmed_received_balance;
        }

        public String getPending_received_balance() {
            return pending_received_balance;
        }

        public void setPending_received_balance(String pending_received_balance) {
            this.pending_received_balance = pending_received_balance;
        }
    }



    public String getStatus() {
        return status;
    }

    public BlockIOData getData() {
        return data;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setData(BlockIOData data) {
        this.data = data;
    }
}
