package com.generalbytes.batm.server.extensions.extra.common;

import com.generalbytes.batm.server.extensions.payment.IBlockchainWatcherAddressListener;

import java.util.ArrayList;
import java.util.List;

public class AddressWatchRecord {
    private String cryptoCurrency;
    private String address;
    private IBlockchainWatcherAddressListener listener;
    private List<String> lastTransactionIds = new ArrayList<>();

    public AddressWatchRecord(String cryptoCurrency, String address, IBlockchainWatcherAddressListener listener) {
        this.cryptoCurrency = cryptoCurrency;
        this.address = address;
        this.listener = listener;
    }

    public String getCryptoCurrency() {
        return cryptoCurrency;
    }

    public String getAddress() {
        return address;
    }

    public IBlockchainWatcherAddressListener getListener() {
        return listener;
    }

    public List<String> getLastTransactionIds() {
        return lastTransactionIds;
    }
}
