package com.generalbytes.batm.server.extensions.extra.dogecoin.wallets.blockio;

/**
 * Created by b00lean on 8/11/14.
 */
public class BlockIOResponseWithdrawal {
    private String status;
    private BlockIOData data;

    public BlockIOResponseWithdrawal() {
    }

    public class BlockIOData {
        private String error_message;
        private String txid;
        private String available_balance;
        private String max_withdrawal_available;
        private String minimum_balance_needed;
        private String estimated_network_fee;

        private String amount_withdrawn;
        private String amount_sent;
        private String network_fee;
        private String blockio_fee;

        public String getTxid() {
            return txid;
        }

        public void setTxid(String txid) {
            this.txid = txid;
        }

        public String getAmount_withdrawn() {
            return amount_withdrawn;
        }

        public void setAmount_withdrawn(String amount_withdrawn) {
            this.amount_withdrawn = amount_withdrawn;
        }

        public String getAmount_sent() {
            return amount_sent;
        }

        public void setAmount_sent(String amount_sent) {
            this.amount_sent = amount_sent;
        }

        public String getNetwork_fee() {
            return network_fee;
        }

        public void setNetwork_fee(String network_fee) {
            this.network_fee = network_fee;
        }

        public String getBlockio_fee() {
            return blockio_fee;
        }

        public void setBlockio_fee(String blockio_fee) {
            this.blockio_fee = blockio_fee;
        }

        public String getError_message() {
            return error_message;
        }

        public void setError_message(String error_message) {
            this.error_message = error_message;
        }

        public String getAvailable_balance() {
            return available_balance;
        }

        public void setAvailable_balance(String available_balance) {
            this.available_balance = available_balance;
        }

        public String getMax_withdrawal_available() {
            return max_withdrawal_available;
        }

        public void setMax_withdrawal_available(String max_withdrawal_available) {
            this.max_withdrawal_available = max_withdrawal_available;
        }

        public String getMinimum_balance_needed() {
            return minimum_balance_needed;
        }

        public void setMinimum_balance_needed(String minimum_balance_needed) {
            this.minimum_balance_needed = minimum_balance_needed;
        }

        public String getEstimated_network_fee() {
            return estimated_network_fee;
        }

        public void setEstimated_network_fee(String estimated_network_fee) {
            this.estimated_network_fee = estimated_network_fee;
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
