package com.generalbytes.batm.server.extensions.payment;

public interface IBlockchainWatcherAddressListener {
    void newTransactionSeen(String cryptoCurrency, String address, String transactionId, int confirmations, Object tag);
}
