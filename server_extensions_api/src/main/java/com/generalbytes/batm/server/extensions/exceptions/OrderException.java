package com.generalbytes.batm.server.extensions.exceptions;

/**
 * Thrown when a crypto order operation fails.
 *
 * <p>Raised by
 * {@link com.generalbytes.batm.server.extensions.IExtensionContext#createOrder} (e.g. invalid
 * parameters, blacklisted identity/phone, amount over limits, unknown identity) and by
 * {@link com.generalbytes.batm.server.extensions.IExtensionContext#redeemOrder} (e.g. unknown
 * order id, wrong status, downstream BUY submission failed).</p>
 */
public class OrderException extends Exception {
    public OrderException(String message) {
        super(message);
    }
}
