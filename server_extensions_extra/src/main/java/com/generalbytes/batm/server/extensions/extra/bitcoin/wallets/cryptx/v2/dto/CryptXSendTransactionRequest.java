package com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.cryptx.v2.dto;

public class CryptXSendTransactionRequest {

    private String address;

    private String value;

    private String uniqueId;

    private int blocksSize;

    private boolean subtractFeeFromOutputs;

    public CryptXSendTransactionRequest(String address, String value, String uniqueId, int blocksSize) {
        this.address = address;
        this.value = value;
        this.uniqueId = uniqueId;
        this.blocksSize = blocksSize != 0 ? blocksSize : 2;
        this.subtractFeeFromOutputs = false;
    }

    public String getAddress() {
        return address;
    }

    public String getValue() {
        return value;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public int getBlocksSize() {
        return blocksSize;
    }

    public boolean isSubtractFeeFromOutputs() {
        return subtractFeeFromOutputs;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public void setBlocksSize(int blocksSize) {
        this.blocksSize = blocksSize;
    }

    public void setSubtractFeeFromOutputs(boolean subtractFeeFromOutputs) {
        this.subtractFeeFromOutputs = subtractFeeFromOutputs;
    }
}
