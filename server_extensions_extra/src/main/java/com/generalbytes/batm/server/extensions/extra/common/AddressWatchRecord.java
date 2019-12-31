package com.generalbytes.batm.server.extensions.extra.common;

import com.generalbytes.batm.server.extensions.payment.IBlockchainWatcherAddressListener;

import java.util.ArrayList;
import java.util.List;

public class AddressWatchRecord {
    private String cryptoCurrency;
    private String address;
    private Object tag;
    private IBlockchainWatcherAddressListener listener;
    private List<String> lastTransactionIds = new ArrayList<>();

    public AddressWatchRecord(String cryptoCurrency, String address, IBlockchainWatcherAddressListener listener, Object tag) {
        this.cryptoCurrency = cryptoCurrency;
        this.address = address;
        this.tag = tag;
        this.listener = listener;
    }

    public String getCryptoCurrency() {
        return cryptoCurrency;
    }

    public String getAddress() {
        return address;
    }

    public Object getTag() {
        return tag;
    }

    public IBlockchainWatcherAddressListener getListener() {
        return listener;
    }

    public List<String> getLastTransactionIds() {
        return lastTransactionIds;
    }
}
