package com.generalbytes.batm.server.extensions;

import java.math.BigDecimal;

/**
 * Represents an exchange that supports Bitcoin transactions over the Lightning Network.
 */
public interface ILightningExchange {

    /**
     * Generates a Lightning Network invoice (payment request) for a specified amount of Bitcoin.
     *
     * @param cryptoAmount The amount of Bitcoin to request in the invoice.
     * @return The serialized invoice string.
     */
    String getInvoice(BigDecimal cryptoAmount);

}