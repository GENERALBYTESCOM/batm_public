package com.generalbytes.batm.server.extensions.order;

import java.math.BigDecimal;

/**
 * Parameters for creating a crypto order.
 * @see com.generalbytes.batm.server.extensions.IExtensionContext#createOrder(ICreateOrderRequest)
 */
public interface ICreateOrderRequest {

    /** Required. For crypto/exchange config + limits. */
    String getTerminalSerialNumber();

    /** Required. Fiat amount, must be &gt; 0. */
    BigDecimal getFiatAmount();

    /** Required. Fiat currency code (e.g. "USD"). */
    String getFiatCurrency();

    /** Required. Crypto currency code (e.g. "BTC"). */
    String getCryptoCurrency();

    /** Required. Destination crypto address. */
    String getDestinationAddress();

    /** Required. Public ID of the identity that owns the order. */
    String getIdentityPublicId();

    /** Optional. */
    String getDiscountCode();
}
