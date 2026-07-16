package com.generalbytes.batm.server.extensions.order;

import com.generalbytes.batm.server.extensions.ITransactionDetails;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * Information about a newly created crypto order.
 *
 * @see com.generalbytes.batm.server.extensions.IExtensionContext#createOrder(ICreateOrderRequest)
 */
public interface IOrderInfo {

    /** The main transaction identifier (RID). Used for redeem. */
    String getTransactionId();

    /** Transaction status. See {@link ITransactionDetails} ORDER status constants. */
    int getStatus();

    /** Customer uses this at the Safe to deposit cash. Equals {@link #getTransactionId()}. */
    String getDepositCode();

    /** Fiat amount. */
    BigDecimal getFiatAmount();

    /** Fiat currency code (e.g. "USD"). */
    String getFiatCurrency();

    /** Crypto amount calculated by the server from the fiat amount at order creation time. */
    BigDecimal getCryptoAmount();

    /** Crypto currency code (e.g. "BTC"). */
    String getCryptoCurrency();

    /** Destination crypto address bound to this order. */
    String getCryptoAddress();

    /** Identity id of the person the order was created for. */
    String getIdentityPublicId();

    /** Server time by which the order must be picked up (deposited) before it expires. */
    OffsetDateTime getExpiryServerTime();
}
