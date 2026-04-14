package com.generalbytes.batm.server.extensions;

import java.math.BigDecimal;

/**
 * Holds the profit evaluation context and the resulting evaluated profit percentage.
 *
 * @see ITerminalListener#overrideBuyProfit(ProfitOverrideContext)
 * @see ITerminalListener#overrideSellProfit(ProfitOverrideContext)
 */
public interface ProfitOverrideContext {

    /**
     * The serial number of the terminal where the transaction is taking place.
     *
     * @return the serial number of the terminal
     */
    String getTerminalSerialNumber();

    /**
     * The cryptocurrency involved in the transaction (e.g. BTC, ETH, LTC).
     *
     * @return cryptocurrency code
     */
    String getCryptocurrency();

    /**
     * The payment method used by the customer for this transaction, or {@code null} if it is not known.
     *
     * @return the payment method, or {@code null}
     * @see TransactionPaymentMethod
     */
    TransactionPaymentMethod getPaymentMethod();

    /**
     * The public ID of the customer identity associated with this transaction,
     * or {@code null} if the identity is not yet known at the time of evaluation or is anonymous.
     *
     * @return identity public ID, or {@code null}
     */
    String getIdentityPublicId();

    /**
     * The fiat amount of the transaction.
     *
     * @return fiat amount
     */
    BigDecimal getFiatAmount();

    /**
     * The fiat currency used in the transaction (e.g. USD, EUR, CZK).
     *
     * @return fiat currency code
     */
    String getFiatCurrency();

    /**
     * The profit percentage that has been evaluated from the configuration or previous extensions.
     * This is the value that will be used if no override is provided.
     *
     * @return the evaluated profit percentage
     */
    BigDecimal getEvaluatedProfitPercentage();
}
