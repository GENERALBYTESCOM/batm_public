package com.generalbytes.batm.server.extensions.extra.dogecoin.wallets.blockio;

/**
 * Created by b00lean on 8/11/14.
 */
public class BlockIOResponseAddresses {
    private String status;
    private BlockIOData data;

    public BlockIOResponseAddresses() {
    }

    public class BlockIOData {
        private String network;
        private BlockIOAddress[] addresses;

        public BlockIOData() {
        }

        public String getNetwork() {
            return network;
        }

        public BlockIOAddress[] getAddresses() {
            return addresses;
        }

        public void setNetwork(String network) {
            this.network = network;
        }

        public void setAddresses(BlockIOAddress[] addresses) {
            this.addresses = addresses;
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
