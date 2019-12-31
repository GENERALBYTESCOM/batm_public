package com.generalbytes.batm.server.extensions.extra.common;

import com.generalbytes.batm.server.extensions.payment.IBlockchainWatcherTransactionListener;

public class TransactionWatchRecord {
    private String cryptoCurrency;
    private String transactionHash;
    private IBlockchainWatcherTransactionListener listener;
    private Object tag;
    private int lastNumberOfConfirmations;

    public TransactionWatchRecord(String cryptoCurrency, String transactionHash, IBlockchainWatcherTransactionListener listener, Object tag, int lastNumberOfConfirmations) {
        this.cryptoCurrency = cryptoCurrency;
        this.transactionHash = transactionHash;
        this.listener = listener;
        this.tag = tag;
        this.lastNumberOfConfirmations = lastNumberOfConfirmations;
    }

    public String getTransactionHash() {
        return transactionHash;
    }

    public Object getTag() {
        return tag;
    }

    public IBlockchainWatcherTransactionListener getListener() {
        return listener;
    }

    public String getCryptoCurrency() {
        return cryptoCurrency;
    }

    public int getLastNumberOfConfirmations() {
        return lastNumberOfConfirmations;
    }

    public void setLastNumberOfConfirmations(int lastNumberOfConfirmations) {
        this.lastNumberOfConfirmations = lastNumberOfConfirmations;
    }
}
