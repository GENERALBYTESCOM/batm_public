package com.generalbytes.batm.server.extensions;

import java.math.BigDecimal;

/**
 * Data of a wallet payment that spends funds from a private key scanned from a wallet at the terminal
 * to pay out a sell transaction, successfully sent by the server.
 */
public class ScannedWalletSellPaymentData {
    private final String remoteTransactionId;
    private final String cryptocurrency;
    private final BigDecimal amount;
    private final String sourceAddress;
    private final String destinationAddress;
    private final String transactionHash;
    private final String privateKey;

    public ScannedWalletSellPaymentData(String remoteTransactionId,
                                        String cryptoCurrency,
                                        BigDecimal amount,
                                        String sourceAddress,
                                        String destinationAddress,
                                        String transactionHash,
                                        String privateKey
    ) {
        this.remoteTransactionId = remoteTransactionId;
        this.cryptocurrency = cryptoCurrency;
        this.amount = amount;
        this.sourceAddress = sourceAddress;
        this.destinationAddress = destinationAddress;
        this.transactionHash = transactionHash;
        this.privateKey = privateKey;
    }

    /**
     * Remote transaction ID of the related sell transaction, or {@code null} if the wallet payment
     * could not be matched to one.
     */
    public String getRemoteTransactionId() {
        return remoteTransactionId;
    }

    /**
     * Cryptocurrency of this wallet payment.
     */
    public String getCryptocurrency() {
        return cryptocurrency;
    }

    /**
     * Amount sent in this wallet payment.
     */
    public BigDecimal getAmount() {
        return amount;
    }

    /**
     * Address the payment was sent from, derived from the {@link #getPrivateKey()}.
     */
    public String getSourceAddress() {
        return sourceAddress;
    }

    /**
     * Address the payment was sent to.
     */
    public String getDestinationAddress() {
        return destinationAddress;
    }

    /**
     * Hash of the broadcast transaction.
     */
    public String getTransactionHash() {
        return transactionHash;
    }

    /**
     * Private key used for this wallet payment.
     */
    public String getPrivateKey() {
        return privateKey;
    }

    /**
     * Overridden to exclude {@link #getPrivateKey()}.
     */
    @Override
    public String toString() {
        return "ScannedWalletSellPaymentData{" +
                  "remoteTransactionId='" + remoteTransactionId + '\'' +
                  ", cryptoCurrency='" + cryptocurrency + '\'' +
                  ", amount=" + amount +
                  ", destinationAddress='" + destinationAddress + '\'' +
                  ", sourceAddress='" + sourceAddress + '\'' +
                  ", transactionHash='" + transactionHash + '\'' +
               '}';
    }
}
