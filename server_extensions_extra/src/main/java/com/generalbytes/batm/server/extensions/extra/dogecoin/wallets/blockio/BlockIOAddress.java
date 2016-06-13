package com.generalbytes.batm.server.extensions.extra.dogecoin.wallets.blockio;

/**
 * Created by b00lean on 8/11/14.
 */
public class BlockIOAddress {
    private int user_id;
    private String address;
    private String label;
    private String available_balance;
    private String unconfirmed_sent_balance;
    private String unconfirmed_received_balance;
    private String pending_received_balance;

    public BlockIOAddress() {
    }

    public int getUser_id() {
        return user_id;
    }

    public String getAddress() {
        return address;
    }

    public String getLabel() {
        return label;
    }

    public String getUnconfirmed_sent_balance() {
        return unconfirmed_sent_balance;
    }

    public String getUnconfirmed_received_balance() {
        return unconfirmed_received_balance;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setUnconfirmed_sent_balance(String unconfirmed_sent_balance) {
        this.unconfirmed_sent_balance = unconfirmed_sent_balance;
    }

    public void setUnconfirmed_received_balance(String unconfirmed_received_balance) {
        this.unconfirmed_received_balance = unconfirmed_received_balance;
    }

    public String getAvailable_balance() {
        return available_balance;
    }

    public void setAvailable_balance(String available_balance) {
        this.available_balance = available_balance;
    }

    public String getPending_received_balance() {
        return pending_received_balance;
    }

    public void setPending_received_balance(String pending_received_balance) {
        this.pending_received_balance = pending_received_balance;
    }
}