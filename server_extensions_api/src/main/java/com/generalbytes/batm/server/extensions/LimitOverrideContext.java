package com.generalbytes.batm.server.extensions;

/**
 * Describes the transaction for which the limits are about to be calculated.
 *
 * @see ITransactionListener#overrideTransactionLimits(LimitOverrideContext)
 */
public interface LimitOverrideContext {

    /**
     * Direction of the transaction the limits are calculated for.
     */
    enum Direction {
        BUY,
        SELL
    }

    /**
     * The serial number of the terminal where the transaction is taking place.
     *
     * @return the serial number of the terminal
     */
    String getTerminalSerialNumber();

    /**
     * The public ID of the customer identity associated with the transaction,
     * or {@code null} if the customer is anonymous.
     *
     * @return identity public ID, or {@code null}
     */
    String getIdentityPublicId();

    /**
     * Whether the customer buys or sells crypto.
     *
     * @return the transaction direction
     */
    Direction getDirection();

    /**
     * The cryptocurrency involved in the transaction (e.g. BTC, ETH, LTC).
     *
     * @return cryptocurrency code
     */
    String getCryptocurrency();

    /**
     * The destination crypto address, or {@code null} if it is not known at this point (e.g. in the sell flow).
     *
     * @return crypto address, or {@code null}
     */
    String getCryptoAddress();

    /**
     * The payment type used by the customer for this transaction, or {@code null} if it is not known.
     * It is always {@link TransactionPaymentType#CASH} for sell transactions.
     *
     * @return the payment type, or {@code null}
     * @see TransactionPaymentType
     */
    TransactionPaymentType getPaymentType();

    /**
     * The local ID of the transaction being prepared, or {@code null} if it is not known at this point.
     * It stays the same for the whole transaction, so it can be used to correlate calls for one customer session.
     *
     * @return local transaction ID, or {@code null}
     */
    String getLocalTransactionId();
}
