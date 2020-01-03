package com.generalbytes.batm.server.extensions.extra.common;

import com.generalbytes.batm.server.extensions.payment.IBlockchainWatcherTransactionListener;

public class TransactionWatchRecord {
    private String cryptoCurrency;
    private String transactionHash;
    private IBlockchainWatcherTransactionListener listener;
    private int lastNumberOfConfirmations;

    public TransactionWatchRecord(String cryptoCurrency, String transactionHash, IBlockchainWatcherTransactionListener listener, int lastNumberOfConfirmations) {
        this.cryptoCurrency = cryptoCurrency;
        this.transactionHash = transactionHash;
        this.listener = listener;
        this.lastNumberOfConfirmations = lastNumberOfConfirmations;
    }

    public String getTransactionHash() {
        return transactionHash;
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
