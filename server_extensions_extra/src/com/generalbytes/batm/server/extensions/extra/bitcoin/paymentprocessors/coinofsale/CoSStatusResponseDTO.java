package com.generalbytes.batm.server.extensions.extra.bitcoin.paymentprocessors.coinofsale;

/**
 * Created by b00lean on 3/8/15.
 */
public class CoSStatusResponseDTO {
    private String address;
    private String txid;
    private String status; // paid/unpaid/error

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTxid() {
        return txid;
    }

    public void setTxid(String txid) {
        this.txid = txid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
